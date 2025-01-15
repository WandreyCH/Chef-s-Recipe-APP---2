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
        recipeName = findViewById(R.id.recipeName);
        recipeDescription = findViewById(R.id.recipeDescription);
        chefName3= findViewById(R.id.chefName);
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");


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
                    List<Recipe> fetchedRecipes = new ArrayList<>();

                    // Iterar sobre os dados para adicionar as receitas na lista
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String description = snapshot.child("description").getValue(String.class);
                        String chefName = snapshot.child("chefName").getValue(String.class);

                        Recipe recipe = new Recipe(name, description, chefName);
                        fetchedRecipes.add(recipe);
                    }
                    // Atualizar a lista de receitas no adapter
                    recipeList.clear();
                    recipeList.addAll(fetchedRecipes);
                    recipeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Tratar erro de leitura
                    Log.e("FirebaseError", "Erro ao acessar o Firebase: " + databaseError.toException());
                }
            });
        }
}
