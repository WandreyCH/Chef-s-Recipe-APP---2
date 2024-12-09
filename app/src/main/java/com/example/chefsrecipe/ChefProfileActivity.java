package com.example.chefsrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChefProfileActivity extends AppCompatActivity {

    ImageView chefImage;
    EditText chefDescription;
    Button saveProfileButton, createRecipeButton, profileUpdate, buttonLogout;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    TextView savedDescriptionText;
    TextView chefNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_profile);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        chefImage = findViewById(R.id.chefImage);
        chefDescription = findViewById(R.id.chefDescription);
        savedDescriptionText = findViewById(R.id.savedDescriptionText);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        createRecipeButton = findViewById(R.id.createRecipeButton);
        profileUpdate = findViewById(R.id.updateProfile);
        buttonLogout = findViewById(R.id.logoutButton);
        chefNameText = findViewById(R.id.chefNameText);

        // Carregar o nome do chef ao iniciar a atividade
        loadChefProfile();

        profileUpdate.setOnClickListener(v -> update());

        buttonLogout.setOnClickListener(v -> logout());

        saveProfileButton.setOnClickListener(v -> saveProfile());

        createRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChefProfileActivity.this, CreateRecipeActivity.class);
            startActivity(intent);
        });
    }


    private void loadChefProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Recuperar os dados do usuário do Firebase
            databaseReference.child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Chef chef = task.getResult().getValue(Chef.class);
                    if (chef != null) {
                        chefNameText.setText(chef.getName()); // Define o nome do chef no TextView
                        if (chef.getChefDescription() != null) {
                            savedDescriptionText.setText(chef.getChefDescription()); // Define a descrição salva
                            savedDescriptionText.setVisibility(View.VISIBLE); // Torna a descrição visível
                        }
                    } else {
                        Toast.makeText(ChefProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChefProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveProfile() {
        String description = chefDescription.getText().toString().trim();

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Atualizar apenas a descrição no banco de dados
            databaseReference.child(userId).child("chefDescription").setValue(description)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Atualiza o TextView instantaneamente com a descrição salva
                            savedDescriptionText.setText(description);
                            savedDescriptionText.setVisibility(View.VISIBLE);
                            Toast.makeText(ChefProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChefProfileActivity.this, "Failed to Update Profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    // Método de logout
    private void logout() {
        mAuth.signOut();  // Desconecta o usuário do Firebase
        Intent intent = new Intent(ChefProfileActivity.this, LoginScreenActivity.class); // Direciona para a tela de login
        startActivity(intent);
        finish();  // Finaliza a atividade atual
    }
    private void update() {
        Intent intent = new Intent(ChefProfileActivity.this, UpdateScreenActivity.class);
        startActivity(intent);
        finish();
    }
}
