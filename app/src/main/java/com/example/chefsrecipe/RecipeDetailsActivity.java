package com.example.chefsrecipe;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.RequestBody;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;

public class RecipeDetailsActivity extends AppCompatActivity {

    private static final String API_KEY = "nghgU/xbrOsjWJHm8R/amA==78Hk0ZQil4itXi9X";

    private TextView recipeName, recipeDescription,
            chefName, ingredients, preparation;

    private EditText recipeComment;
    private Button saveProfileButton;
    private RatingBar ratingBar; // Declare o RatingBar
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText ingredientEditText;
    private Button searchCaloriesButton;
    private TextView caloriesResult;

    private OkHttpClient client;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        recipeName = findViewById(R.id.recipeName);
        recipeDescription = findViewById(R.id.recipeDescription);
        chefName = findViewById(R.id.chefName);
        ingredients = findViewById(R.id.ingredients);
        preparation = findViewById(R.id.preparation);
        recipeComment = findViewById(R.id.recipeComment);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        ratingBar = findViewById(R.id.ratingBar); // Inicialize o RatingBar
        ingredientEditText = findViewById(R.id.ingredientEditText);
        searchCaloriesButton = findViewById(R.id.searchCaloriesButton);
        caloriesResult = findViewById(R.id.caloriesResult);

        client = new OkHttpClient();
        gson = new Gson();

        // Inicializando Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Receba os dados passados via Intent
        String name = getIntent().getStringExtra("recipeName");
        String description = getIntent().getStringExtra("recipeDescription");
        String chef = getIntent().getStringExtra("chefName");
        String ingredientList = getIntent().getStringExtra("ingredients");
        String prep = getIntent().getStringExtra("preparation");

        // Defina os valores nas TextViews
        recipeName.setText(name);
        recipeDescription.setText("Description: " + description);
        chefName.setText("Chef: " + chef);
        ingredients.setText("Ingredients: " + ingredientList);
        preparation.setText("Preparation Method: " + prep);

        searchCaloriesButton.setOnClickListener(v -> {
            String ingredient = ingredientEditText.getText().toString().trim();

            if (!ingredient.isEmpty()) {
                // Chama a API para obter as calorias do ingrediente
                searchCalories(ingredient);
            } else {
                Toast.makeText(RecipeDetailsActivity.this, "Please enter an ingredient", Toast.LENGTH_SHORT).show();
            }
        });

        // Obter o role do usuário do Firebase
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(userId)
                .child("role").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String role = dataSnapshot.getValue(String.class);

                        if ("Home Cook".equals(role)) {
                            // Torna visível a caixa de comentário, o RatingBar e o botão apenas se for "Home Cook"
                            recipeComment.setVisibility(View.VISIBLE);
                            ratingBar.setVisibility(View.VISIBLE); // Torna o RatingBar visível
                            saveProfileButton.setVisibility(View.VISIBLE);
                        } else {
                            // Caso contrário, mantém-se invisíveis
                            recipeComment.setVisibility(View.GONE);
                            ratingBar.setVisibility(View.GONE); // Torna o RatingBar invisível
                            saveProfileButton.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(RecipeDetailsActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // Adiciona a lógica para salvar a avaliação quando o botão for clicado
        saveProfileButton.setOnClickListener(v -> {
            // Obtém a avaliação do RatingBar
            float rating = ratingBar.getRating();

            // Obtém o comentário do usuário
            String comment = recipeComment.getText().toString().trim();

            // Armazena a avaliação e o comentário no Firebase
            if (rating > 0 && !comment.isEmpty()) {
                mDatabase.child("RecipeReviews").child(userId).push().setValue(new Review(rating, comment))
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(RecipeDetailsActivity.this, "Review saved successfully!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(RecipeDetailsActivity.this, "Error saving review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(RecipeDetailsActivity.this, "Please add a comment and rating.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //---------------- API -------------- //
    // Lógica para pesquisar no campo de pesquisa
    private void searchCalories(String ingredient) {
        // URL da API de nutrição (substitua pelo seu endpoint correto)
        String url = "https://api.calorieninjas.com/v1/nutrition?query=" + ingredient;

        // Criação da requisição
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Api-Key", API_KEY)
                .build();

        // Executar a requisição de forma assíncrona
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    // Lê a resposta e converte para o objeto NutritionItem
                    String responseData = response.body().string();
                    if (responseData != null && !responseData.isEmpty()) {
                        // Usando Gson para parse da resposta
                        Gson gson = new Gson();
                        JsonObject jsonResponse = gson.fromJson(responseData, JsonObject.class);
                        JsonArray items = jsonResponse.getAsJsonArray("items");

                        // Exibindo os dados de calorias
                        if (items != null && items.size() > 0) {
                            NutritionItem nutritionItem = gson.fromJson(items.get(0), NutritionItem.class);
                            runOnUiThread(() -> {
                                if (nutritionItem != null) {
                                    caloriesResult.setText("Calories: " + nutritionItem.getCalories());
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                caloriesResult.setText("No data available");
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(RecipeDetailsActivity.this, "No data available", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(RecipeDetailsActivity.this, "Error retrieving data: " + response.code(), Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(RecipeDetailsActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }


    // Classe para armazenar os dados da avaliação
    public static class Review {
        public float rating;
        public String comment;

        public Review() {
        }

        public Review(float rating, String comment) {
            this.rating = rating;
            this.comment = comment;
        }
    }
}
