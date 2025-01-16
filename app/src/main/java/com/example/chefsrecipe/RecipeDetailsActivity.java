package com.example.chefsrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


public class RecipeDetailsActivity extends AppCompatActivity {

    private TextView recipeName, recipeDescription,
            chefName, ingredients, preparation;

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

        // Receba os dados passados via Intent
        String name = getIntent().getStringExtra("recipeName");
        String description = getIntent().getStringExtra("recipeDescription");
        String chef = getIntent().getStringExtra("chefName");
        String ingredientList = getIntent().getStringExtra("ingredients");
        String prep = getIntent().getStringExtra("preparation");

        // Defina os valores nas TextViews
        recipeName.setText(name);
        recipeDescription.setText("Description: " + description);
        chefName.setText("Chef: " +chef);
        ingredients.setText("Ingredients: " +ingredientList);
        preparation.setText("Preparation Method: " +prep);


    }
}
