package com.example.chefsrecipe;

public class Review {
    private float rating;
    private String comment;

    public Review() {
        // Constructor vazio necessário para o Firebase
    }

    public Review(float rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
