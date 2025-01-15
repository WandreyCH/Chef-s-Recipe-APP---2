package com.example.chefsrecipe;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReference;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;

    EditText searchBar;
    TextView topRatedTitle, recipeName1, recipeName2, recipeName3,
            recipeDescription1, recipeDescription2, recipeDescription3,
            chefName1, chefName2, chefName3;
    RatingBar ratingBar1;
    Button apiTestButton;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Inicialize componentes (pode ser útil para lógica futura)
        searchBar = findViewById(R.id.searchBar);
        topRatedTitle = findViewById(R.id.topRatedTitle);
        recipeName1 = findViewById(R.id.recipeName1);
        recipeName2 = findViewById(R.id.recipeName2);
        recipeName3 = findViewById(R.id.recipeName3);
        recipeDescription1 = findViewById(R.id.recipeDescription1);
        recipeDescription2 = findViewById(R.id.recipeDescription2);
        recipeDescription3 = findViewById(R.id.recipeDescription3);
        chefName1 = findViewById(R.id.chefName1);
        chefName2 = findViewById(R.id.chefName2);
        chefName3 = findViewById(R.id.chefName3);
//        ratingBar1 = findViewById(R.id.ratingBar1);
        apiTestButton = findViewById(R.id.ButtonApiTests);

        recyclerView = findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeList = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipeList);
        recyclerView.setAdapter(recipeAdapter);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        apiTestButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, TestApiActivity.class);
            startActivity(intent);
        });

        // Inicializa o Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");

        // Buscar receitas aleatórias do firebase
        fetchRecipesFromFirebase();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            navigateToProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Navega para o perfil de Chef ou Home Cook, dependendo do usuário.
    private void navigateToProfile() {
        if (currentUser != null) {
            getRole(currentUser.getUid(), role -> {  // Passa o callback como argumento
                if ("Chef".equals(role)) {
                    Intent intent = new Intent(HomeScreenActivity.this, ChefProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeScreenActivity.this, HomeCookProfile.class);
                    startActivity(intent);
                }
            });
        }
    }
    private void getRole(String userId, RoleCallback callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.child("role").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String role = task.getResult().getValue(String.class);
                if (role != null) {
                    callback.onRoleReceived(role);  // Chama o callback com o valor do role
                } else {
                    callback.onRoleReceived("Home Cook");  // Valor padrão se nulo
                }
            } else {
                callback.onRoleReceived("Home Cook");  // Valor padrão em caso de erro
            }
        });
    }
    // Função que define o método onRoleReceived após "role" ser atualizado no firebase
    public interface RoleCallback {
        void onRoleReceived(String role);
    }

//------------------------------

        private void fetchRecipesFromFirebase() {
            // Buscar todas as receitas
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Criar uma lista para armazenar as receitas
                    List<Recipe> recipeList = new ArrayList<>();

                    // Iterar sobre os dados para adicionar as receitas na lista
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Recipe recipe = snapshot.getValue(Recipe.class);
                        recipeList.add(recipe);
                    }

                    // Selecionar aleatoriamente 3 receitas
                    List<Recipe> randomRecipes = getRandomRecipes(recipeList, 3);

                    // Atualizar a UI com as 3 receitas aleatórias
                    updateUI(randomRecipes);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Tratar erro de leitura
                    Log.e("FirebaseError", "Erro ao acessar o Firebase: " + databaseError.getMessage());
                }
            });
        }

        // Função para selecionar aleatoriamente 3 receitas
        public List<Recipe> getRandomRecipes(List<Recipe> recipeList, int count) {
            Collections.shuffle(recipeList); // Embaralha a lista
            return recipeList.subList(0, Math.min(count, recipeList.size())); // Retorna as primeiras 3 receitas (ou menos se não houver 3 receitas)
        }

        // Função para atualizar a UI com as receitas
        public void updateUI(List<Recipe> randomRecipes) {
            // Aqui você pode atualizar a interface com os dados das 3 receitas aleatórias
            // Por exemplo, se você tem 3 TextViews ou outros componentes de UI para exibir as receitas:

            if (randomRecipes.size() > 0) {
                Recipe recipe1 = randomRecipes.get(0);
                // Atualiza a UI para exibir recipe1
                TextView recipeName1 = findViewById(R.id.recipeName1);
                recipeName1.setText(recipe1.getName());
            }

            if (randomRecipes.size() > 1) {
                Recipe recipe2 = randomRecipes.get(1);
                // Atualiza a UI para exibir recipe2
                TextView recipeName2 = findViewById(R.id.recipeName2);
                recipeName2.setText(recipe2.getName());
            }

            if (randomRecipes.size() > 2) {
                Recipe recipe3 = randomRecipes.get(2);
                // Atualiza a UI para exibir recipe3
                TextView recipeName3 = findViewById(R.id.recipeName3);
                recipeName3.setText(recipe3.getName());
            }
        }
    }