package com.example.chefsrecipe;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipes = new ArrayList<>();

    public RecipeAdapter(List<Recipe> filteredRecipes, Object navigateToRecipeDetails) {
        this.recipes = filteredRecipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);

        Log.d("RecipeAdapter", "Name: " + recipe.getName());
        Log.d("RecipeAdapter", "Description: " + recipe.getDescription());
        Log.d("RecipeAdapter", "Chef: " + recipe.getChefName());

        // Bind data to TextViews
        holder.name.setText(recipe.getName());
        holder.description.setText("Description: " + recipe.getDescription());
        holder.description.setTextSize(18);
        holder.chefName.setText("Chef: "+ recipe.getChefName());
        holder.chefName.setTextSize(18);

    }
    @Override
    public int getItemCount() {
        return recipes.size(); // Retorna o número de receitas na lista
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView description;
        TextView chefName;
        TextView ingredients;
        TextView preparation;



        public RecipeViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recipeName);
            description = itemView.findViewById((R.id.recipeDescription));
            chefName = itemView.findViewById(R.id.chefName);


            Log.d("RecipeViewHolder", "name: " + name);
            Log.d("RecipeViewHolder", "description: " + description);
            Log.d("RecipeViewHolder", "chefName: " + chefName);


        }

    }

}
