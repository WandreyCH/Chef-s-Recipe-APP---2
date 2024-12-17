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

public class HomeCookProfile extends AppCompatActivity {

    ImageView hcImage;
    EditText hcAge;
    Button saveProfileButton, profileUpdate, buttonLogout;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    TextView savedAgeText;
    TextView hcNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_cook_profile);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        hcImage = findViewById(R.id.hcImage);
        hcAge = findViewById(R.id.hcAge);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        profileUpdate = findViewById(R.id.updateProfile);
        buttonLogout = findViewById(R.id.logoutButton);
        hcNameText = findViewById(R.id.hcNameText);

        // Carregar o nome do chef ao iniciar a atividade
        loadHomeCookProfile();

        profileUpdate.setOnClickListener(v -> update());

        buttonLogout.setOnClickListener(v -> logout());

        saveProfileButton.setOnClickListener(v -> saveProfile());

    }


    private void loadHomeCookProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Recuperar os dados do usuário do Firebase
            databaseReference.child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = task.getResult().getValue(User.class);
                    if (user != null) {
                        hcNameText.setText(user.getName());  // Define o nome do chef no TextView
                    } else {
                        Toast.makeText(HomeCookProfile.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeCookProfile.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveProfile() {
        String ageInput = hcAge.getText().toString().trim();  // Captura a idade como String

        if (ageInput.isEmpty()) {
            Toast.makeText(this, "Please enter an age", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageInput);  // Converte a idade para int

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();

                // Cria um objeto HomeCook e define a idade
                HomeCook homeCookProfile = new HomeCook();
                homeCookProfile.setAge(age);  // Usa o valor convertido

                // Salva o objeto HomeCook no banco de dados do Firebase
                databaseReference.child(userId).child("homeCookProfile").setValue(homeCookProfile)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                savedAgeText.setText(String.valueOf(age));  // Atualiza o TextView com a idade salva
                                savedAgeText.setVisibility(View.VISIBLE);  // Torna o TextView visível
                                Toast.makeText(HomeCookProfile.this, "Profile Saved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeCookProfile.this, "Failed to Save Profile", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } catch (NumberFormatException e) {
            // Trata o caso em que a entrada não é um número válido
            Toast.makeText(this, "Please enter a valid number for age", Toast.LENGTH_SHORT).show();
        }
    }
    // Método de logout
    private void logout() {
        mAuth.signOut();  // Desconecta o usuário do Firebase
        Intent intent = new Intent(HomeCookProfile.this, LoginScreenActivity.class); // Direciona para a tela de login
        startActivity(intent);
        finish();  // Finaliza a atividade atual
    }
    private void update() {
        Intent intent = new Intent(HomeCookProfile.this, UpdateScreenActivity.class);
        startActivity(intent);
        finish();
    }
}