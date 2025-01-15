package com.example.chefsrecipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;

    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
            Recipe recipe = recipeList.get(position);
            holder.recipeName.setText(recipe.getName());
            holder.description.setText(recipe.getDescription());
            holder.name.setText("Chef: " + recipe.getChefName());
            //holder.userComment.setText("User: " + recipe.getComment());
            //holder.ratingBar.setRating(recipe.getRating());
            // Carregar a imagem com Picasso ou Glide, por exemplo

    }
    @Override
    public int getItemCount() {
        return recipeList.size(); // Retorna o número de receitas na lista
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName, userComment, name, description;
        RatingBar ratingBar;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            description = itemView.findViewById((R.id.recipeDescription));
            name = itemView.findViewById(R.id.chefName);

            //userComment = itemView.findViewById(R.id.userComment);
            //ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
