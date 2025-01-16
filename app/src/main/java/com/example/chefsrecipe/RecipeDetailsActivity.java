package com.example.chefsrecipe;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecipeDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recipe_details);

        String name = getIntent().getStringExtra("name");
        if (name != null) {
            fetchRecipeDetails(name);
        }
    }

    private void fetchRecipeDetails(String recipeName) {

        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipes").child("TPsxE19MHWY6DXhkluDdvBkWGp33");
        recipeRef.orderByChild("name").equalTo(recipeName)
                .addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        displayRecipeDetails(recipe);
                    }
                }
            }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e("RecipeDetail", "Error fetching recipe: " + databaseError.getMessage());
        }
    });
}


private void displayRecipeDetails(Recipe recipe) {
    TextView name = findViewById(R.id.recipeName);
    TextView description = findViewById(R.id.recipeDescription);
    TextView ingredients = findViewById(R.id.recipeIngredients);

    name.setText(recipe.getName());
    description.setText(recipe.getDescription());
    ingredients.setText(recipe.getIngredients());
}
}
