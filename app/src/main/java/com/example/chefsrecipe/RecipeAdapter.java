package com.example.chefsrecipe;

import android.content.Intent;
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
        holder.chefName.setText("Chef: " + recipe.getChefName());
        holder.ingredients.setText("Ingredients: " + recipe.getIngredients());
        holder.preparation.setText("Preparation Method: " + recipe.getPreparation());

        // Defina um listener de clique
        holder.itemView.setOnClickListener(v -> {
            // Crie uma intent para a tela de detalhes da receita
            Intent intent = new Intent(v.getContext(), RecipeDetailsActivity.class);
            // Passe os dados da receita para a nova atividade
            intent.putExtra("recipeName", recipe.getName());
            intent.putExtra("recipeDescription", recipe.getDescription());
            intent.putExtra("chefName", recipe.getChefName());
            intent.putExtra("ingredients", recipe.getIngredients());
            intent.putExtra("preparation", recipe.getPreparation());
            v.getContext().startActivity(intent);
        });

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
            ingredients = itemView.findViewById(R.id.ingredients);
            preparation = itemView.findViewById(R.id.preparation);


            Log.d("RecipeViewHolder", "name: " + name);
            Log.d("RecipeViewHolder", "description: " + description);
            Log.d("RecipeViewHolder", "chefName: " + chefName);

            if (name == null || description == null || chefName == null) {
                Log.e("RecipeAdapter", "Erro: Alguns campos não foram encontrados no layout.");
            }

            // Adicionando o OnClickListener para o item
            itemView.setOnClickListener(v -> {
                // Passar os dados da receita para a nova Activity
                Recipe recipe = recipes.get(getAdapterPosition()); // Pega a receita clicada
                Intent intent = new Intent(itemView.getContext(), RecipeDetailsActivity.class); // Nome da nova Activity
                intent.putExtra("recipe_name", recipe.getName());
                intent.putExtra("recipe_description", recipe.getDescription());
                intent.putExtra("recipe_chef", recipe.getChefName());
                // Você pode passar outros dados necessários
                itemView.getContext().startActivity(intent); // Inicia a nova Activity
            });
        }

        }

    }
