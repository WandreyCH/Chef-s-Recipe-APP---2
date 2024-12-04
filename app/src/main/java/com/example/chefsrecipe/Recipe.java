package com.example.chefsrecipe;

public class Recipe {

    private String chefName, name, description, ingredients, preparation;

    public Recipe() {
        // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
    }

    public Recipe(String chefName, String name, String description, String ingredients, String preparation) {
        this.chefName = chefName;
        this.name = name;
        this.description = "";
        this.ingredients = "";
        this.preparation = "";
    }

    public String getChefName() {
        return chefName;
    }

    public void setChefName(String chefName) {
        this.chefName = chefName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = "";
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = "";
    }

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = "";
    }

}
