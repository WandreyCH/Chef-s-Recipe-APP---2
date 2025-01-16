package com.example.chefsrecipe;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

public class RecipeDetailsActivity extends AppCompatActivity {

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
    }

    private void searchCalories(String ingredient) {
        // URL da API de nutrição (substitua pelo seu endpoint correto)
        String url = "https://api.example.com/nutrition?ingredient=" + ingredient;

        // Criação da requisição
        Request request = new Request.Builder()
                .url(url)
                .build();

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


        //---------------- API -------------- //
        // Lógica para pesquisar no campo de pesquisa


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

    // Classe para armazenar os dados da avaliação
    public static class Review {
        public float rating;
        public String comment;

        public Review() { }

        public Review(float rating, String comment) {
            this.rating = rating;
            this.comment = comment;
        }
    }
}