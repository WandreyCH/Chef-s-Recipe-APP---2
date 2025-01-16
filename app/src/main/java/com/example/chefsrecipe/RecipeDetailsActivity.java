package com.example.chefsrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class RecipeDetailsActivity extends AppCompatActivity {

    private TextView recipeName, recipeDescription,
            chefName, ingredients, preparation;

    private EditText recipeComment;
    private Button saveProfileButton;
    private RatingBar ratingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String recipeId; // ID da receita para salvar os dados nela


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_details);


        recipeName = findViewById(R.id.recipeName);
        recipeDescription = findViewById(R.id.recipeDescription);
        chefName = findViewById(R.id.chefName);
        ingredients = findViewById(R.id.ingredients);
        preparation = findViewById(R.id.preparation);
        recipeComment = findViewById(R.id.recipeComment);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        ratingBar = findViewById(R.id.ratingBar);

        // Inicializando Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Receba os dados passados via Intent
        String name = getIntent().getStringExtra("recipeName");
        String description = getIntent().getStringExtra("recipeDescription");
        String chef = getIntent().getStringExtra("chefName");
        String ingredientList = getIntent().getStringExtra("ingredients");
        String prep = getIntent().getStringExtra("preparation");
        recipeId = getIntent().getStringExtra("recipeId");

        // Defina os valores nas TextViews
        recipeName.setText(name);
        recipeDescription.setText("Description: " + description);
        chefName.setText("Chef: " +chef);
        ingredients.setText("Ingredients: " +ingredientList);
        preparation.setText("Preparation Method: " +prep);


        // Obter o role do usuário do Firebase
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(userId)
                .child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String role = dataSnapshot.getValue(String.class);

                if ("Home Cook".equals(role)) {
                    // Torna visível a caixa de comentário e o botão apenas se for "Home Cook"
                    recipeComment.setVisibility(View.VISIBLE);
                    saveProfileButton.setVisibility(View.VISIBLE);
                    ratingBar.setVisibility(View.VISIBLE);
                } else {
                    // Caso contrário, mantêm-se invisíveis
                    recipeComment.setVisibility(View.GONE);
                    saveProfileButton.setVisibility(View.GONE);
                    ratingBar.setVisibility(View.GONE);
                }
            }

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

                Review review = new Review(rating, comment);

                mDatabase.child("Recipes").child(recipeId).child("reviews").push().setValue(review)
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
