package com.example.chefsrecipe;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CreateRecipeActivity extends AppCompatActivity {

    private EditText recipeName, recipeDescription, recipePreparation, ingredientInput;
    private Button saveRecipeButton, addIngredientButton;
    private ListView ingredientsListView;

    private ArrayList<String> ingredientsList; // Lista para armazenar ingredientes
    private ArrayAdapter<String> ingredientsAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        // Firebase setup
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Recipes");

        // UI setup
        recipeName = findViewById(R.id.recipeName);
        recipeDescription = findViewById(R.id.recipeDescription);
        recipePreparation = findViewById(R.id.recipePreparation);
        ingredientInput = findViewById(R.id.ingredientInput);
        addIngredientButton = findViewById(R.id.addIngredientButton);
        ingredientsListView = findViewById(R.id.ingredientsListView);
        saveRecipeButton = findViewById(R.id.saveRecipeButton);

        // Initialize ingredient list and adapter
        ingredientsList = new ArrayList<>();
        ingredientsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ingredientsList);
        ingredientsListView.setAdapter(ingredientsAdapter);

        // Button to add ingredient
        addIngredientButton.setOnClickListener(v -> addIngredient());

        // Save Recipe button
        saveRecipeButton.setOnClickListener(v -> saveRecipe());
    }

    // Add individual ingredient to the list
    private void addIngredient() {
        String ingredient = ingredientInput.getText().toString().trim();
        if (!ingredient.isEmpty()) {
            ingredientsList.add(ingredient);
            ingredientsAdapter.notifyDataSetChanged(); // Update the ListView
            ingredientInput.setText(""); // Clear input field
        } else {
            Toast.makeText(this, "Please enter an ingredient", Toast.LENGTH_SHORT).show();
        }
    }

    // Save recipe to Firebase
    private void saveRecipe() {
        String name = recipeName.getText().toString().trim();
        String description = recipeDescription.getText().toString().trim();
        String preparation = recipePreparation.getText().toString().trim();

        if (name.isEmpty() || description.isEmpty() || preparation.isEmpty() || ingredientsList.isEmpty()) {
            Toast.makeText(this, "All fields and at least one ingredient are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch user's name from Firebase
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    User user = task.getResult().getValue(User.class);
                    if (user != null) {
                        String chefName = user.getName();

                        // Convert ingredient list to a single string
                        String ingredients = String.join(", ", ingredientsList);

                        // Save recipe to Firebase
                        Recipe recipe = new Recipe(name, description, ingredients, preparation, chefName);
                        databaseReference.child(userId).push().setValue(recipe)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(CreateRecipeActivity.this, "Recipe Created", Toast.LENGTH_SHORT).show();
                                        finish(); // Go back or clear form
                                    } else {
                                        Toast.makeText(CreateRecipeActivity.this, "Failed to Create Recipe", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(this, "Chef name not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to load chef profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}