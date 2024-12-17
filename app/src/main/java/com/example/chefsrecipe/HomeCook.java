package com.example.chefsrecipe;

public class HomeCook extends User {
    private int age;

    // Default constructor for Firebase deserialization
    public HomeCook() {
        super();
    }

    public HomeCook(String name, String email, int age) {
        super(name, email, "Home Cook"); // Define "Home Cook" como o role fixo
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}