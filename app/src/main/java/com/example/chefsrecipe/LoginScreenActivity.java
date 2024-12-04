package com.example.chefsrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginScreenActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button buttonLogin;
    private TextView signUpText;

    private FirebaseAuth mAuth;  // Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        signUpText = findViewById(R.id.createAccountText);
        buttonLogin = findViewById(R.id.loginButton);

        // Navigate to SignUpActivity when "Create Account" is clicked
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreenActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Handle login button click
        buttonLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginScreenActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Authenticate the user with Firebase
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Login successful
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(LoginScreenActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginScreenActivity.this, HomeScreenActivity.class);
                    Log.d("LoginActivity", "Navigating to HomeScreenActivity");
                    startActivity(intent);
                    finish();  // Close login activity
                }
            } else {
                // Login failed, display a message
                Toast.makeText(LoginScreenActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}