package com.example.chefsrecipe;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RecipeAdapter recipeAdapter;
    private List<Recipe> recipes;
    private List<Recipe> filteredRecipes;

    EditText searchBar;
    TextView topRatedTitle;
    RatingBar ratingBar1;
    Button apiTestButton;
    RecyclerView recyclerView;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Inicialize componentes (pode ser útil para lógica futura)
        searchBar = findViewById(R.id.searchBar);
        recipes = new ArrayList<>();
        filteredRecipes = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(filteredRecipes, this::navigateToRecipeDetails);
        topRatedTitle = findViewById(R.id.topRatedTitle);


        // Listener para a barra de pesquisa
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRecipes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


//----------------------------------------------

//        ratingBar1 = findViewById(R.id.ratingBar1);
        apiTestButton = findViewById(R.id.ButtonApiTests);


        recyclerView = findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipes = new ArrayList<>();
        recipeAdapter = new RecipeAdapter(recipes, filteredRecipes);
        recyclerView.setAdapter(recipeAdapter);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        apiTestButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, TestApiActivity.class);
            startActivity(intent);
        });

        // Buscar receitas aleatórias do firebase
        fetchRecipes();
        fetchSearchRecipes();
    }

    private void filterRecipes(String query) {
        filteredRecipes.clear();
        for (Recipe recipe : recipes) {
            if (recipe.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredRecipes.add(recipe);
            }
        }
        recipeAdapter.notifyDataSetChanged();
    }

    //Método para buscar as receitas pelo nome
    private void fetchSearchRecipes() {
        DatabaseReference recipesRef = FirebaseDatabase.getInstance()
                .getReference("Recipes").child("TPsxE19MHWY6DXhkluDdvBkWGp33");

        recipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipes.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        recipes.add(recipe);
                    }
                }
                filterRecipes(searchBar.getText().toString()); // Atualiza com o filtro
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Error loading data: " + databaseError.getMessage());
            }
        });
    }

    // Método para navegar para os detalhes da receita (pesquisa)
    private void navigateToRecipeDetails(Recipe recipe) {
        Intent intent = new Intent(this, RecipeDetailsActivity.class);
        intent.putExtra("Recipe", (CharSequence) recipe);
        startActivity(intent);
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

        private void fetchRecipes() {

            // Buscar todas as receitas
            DatabaseReference recipesRef = FirebaseDatabase.getInstance()
                    .getReference("Recipes").child("TPsxE19MHWY6DXhkluDdvBkWGp33");

            recipesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<Recipe> recipeList = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Recipe recipe = snapshot.getValue(Recipe.class);
                        if (recipe != null) {
                            Log.d("RecipeDebug", "Loaded recipe: " + recipe.getName());
                            Log.d("RecipeDebug", "Description: " + recipe.getDescription());
                            Log.d("RecipeDebug", "Ingredients: " + recipe.getIngredients());
                            Log.d("RecipeDebug", "Preparation: " + recipe.getPreparation());
                            Log.d("RecipeDebug", "Chef: " + recipe.getChefName());
                            recipeList.add(recipe);
                        } else {
                            Log.e("RecipeLoaded", "Recipe is null!");
                        }
                    }
                    recipeAdapter.setRecipes(recipeList);
                    recipeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("FirebaseError", "Error loading data: " + databaseError.getMessage());
                    // Handle error
                }
            });
        }
        private void navigateToRecipeDetails(){
            Intent intent = new Intent(HomeScreenActivity.this, RecipeDetailsActivity.class); // Direciona para a tela de login
            startActivity(intent);
            finish();  // Finaliza a atividade atual
        }
}
