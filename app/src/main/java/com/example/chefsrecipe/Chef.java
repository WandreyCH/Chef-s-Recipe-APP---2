package com.example.chefsrecipe;

public class Chef extends User {
    private String chefDescription;

    // Default constructor for Firebase
    public Chef() {
        super();
    }

    public Chef(String name, String email, String chefDescription) {
        super(name, email, "Chef"); // Define "Chef" como o role fixo
        this.chefDescription = chefDescription;
    }

    public String getChefDescription() {
        return chefDescription;
    }

    public void setChefDescription(String chefDescription) {
        this.chefDescription = chefDescription;
    }
}