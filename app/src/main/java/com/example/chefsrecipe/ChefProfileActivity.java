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
    Button saveProfileButton, createRecipeButton;
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
        chefNameText = findViewById(R.id.chefNameText);

        saveProfileButton.setOnClickListener(v -> saveProfile());

        createRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChefProfileActivity.this, CreateRecipeActivity.class);
            startActivity(intent);
        });
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

            // Save the chef's description
            Chef chefProfile = new Chef();
            databaseReference.child(userId).child("chefProfile").setValue(chefProfile)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            savedDescriptionText.setText(description); // Set the saved text
                            savedDescriptionText.setVisibility(View.VISIBLE); // Show the TextView
                            Toast.makeText(ChefProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChefProfileActivity.this, "Failed to Update Profile", Toast.LENGTH_SHORT).show();
                        }
                    });

            databaseReference.child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = task.getResult().getValue(User.class);
                    if (user != null) {
                        chefNameText.setText(user.getName());  // Define o nome do chef no TextView
                    }
                } else {
                    Toast.makeText(ChefProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}