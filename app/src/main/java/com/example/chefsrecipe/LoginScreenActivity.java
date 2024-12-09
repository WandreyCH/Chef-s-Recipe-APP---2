package com.example.chefsrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginScreenActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button buttonLogin;
    private TextView signUpText, forgotPasswordText;

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
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        // Navigate to SignUpActivity when "Create Account" is clicked
        signUpText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreenActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Handle login button click
        buttonLogin.setOnClickListener(v -> loginUser());

        forgotPasswordText.setOnClickListener(v -> showPasswordResetDialog());
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
                Toast.makeText(LoginScreenActivity.this, "Authentication failed: Login or Password are invalid", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //This was created to show a dialog box when you click on "Forgot Password"
    private void showPasswordResetDialog() {
        // Create an AlertDialog to prompt for the email
        EditText emailInput = new EditText(this);
        emailInput.setHint("Enter your email");

        AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(this);
        passwordResetDialog.setTitle("Reset Password");
        passwordResetDialog.setMessage("Enter your email to receive the reset link:");
        passwordResetDialog.setView(emailInput);

        // Set actions for the dialog buttons
        passwordResetDialog.setPositiveButton("Send", (dialog, which) -> {
            String email = emailInput.getText().toString().trim();
            if (!email.isEmpty()) {
                sendPasswordResetEmail(email);
            } else {
                Toast.makeText(LoginScreenActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            }
        });

        passwordResetDialog.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();  // Close the dialog without doing anything
        });

        passwordResetDialog.create().show();  // Display the dialog
    }

    //Envia o link de reset para o email. Está funcional.
    private void sendPasswordResetEmail(String email) {
        // Use Firebase Auth to send the reset link
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginScreenActivity.this, "Reset link sent to your email.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginScreenActivity.this, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}