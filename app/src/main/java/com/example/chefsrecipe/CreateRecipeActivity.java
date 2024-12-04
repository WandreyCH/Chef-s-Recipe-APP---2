package com.example.chefsrecipe;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateRecipeActivity extends AppCompatActivity {

    private EditText recipeName, recipeDescription, recipeIngredients, recipePreparation;
    private Button saveRecipeButton;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");

        recipeName = findViewById(R.id.recipeName);
        recipeDescription = findViewById(R.id.recipeDescription);
        recipeIngredients = findViewById(R.id.recipeIngredients);
        recipePreparation = findViewById(R.id.recipePreparation);
        saveRecipeButton = findViewById(R.id.saveRecipeButton);

        saveRecipeButton.setOnClickListener(v -> saveRecipe());
    }

    private void saveRecipe() {
        String name = recipeName.getText().toString().trim();
        String description = recipeDescription.getText().toString().trim();
        String ingredients = recipeIngredients.getText().toString().trim();
        String preparation = recipePreparation.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || ingredients.isEmpty() || preparation.isEmpty()) {
            Toast.makeText(CreateRecipeActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            Recipe recipe = new Recipe(name, description, ingredients, preparation);

            // Save the recipe
            databaseReference.child(userId).push().setValue(recipe)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateRecipeActivity.this, "Recipe Created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateRecipeActivity.this, "Failed to Create Recipe", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}