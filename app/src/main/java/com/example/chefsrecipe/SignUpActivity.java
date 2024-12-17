package com.example.chefsrecipe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText editTextname, editTextemail, editTextpassword, confirmPassword;
    Spinner spinnerRoller;
    Button buttonSubmit;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference; // Database reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        editTextname = findViewById(R.id.editTextName);
        editTextemail = findViewById(R.id.editTextEmail);
        editTextpassword = findViewById(R.id.editTextPassword);
        confirmPassword = findViewById(R.id.cPassword);
        spinnerRoller = findViewById(R.id.roleSpinner);
        buttonSubmit = findViewById(R.id.submitButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.role_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoller.setAdapter(adapter);


        buttonSubmit.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {


        String email = editTextemail.getText().toString().trim();
        String password = editTextpassword.getText().toString().trim();
        String cPass = confirmPassword.getText().toString().trim();
        String name = editTextname.getText().toString().trim();
        String role = spinnerRoller.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cPass)) {
            Toast.makeText(SignUpActivity.this, "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextemail.setError("Invalid Email");
            editTextemail.requestFocus();
            return;
        }
        if (password.length() < 8) {
            editTextpassword.setError("Password must be at least 8 characters long");
            editTextpassword.requestFocus();
            return;
        }
        if (!password.equals(cPass)) {
            confirmPassword.setError("Passwords do not match");
            confirmPassword.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    User user = new User(name, email, role);

                    databaseReference.child(userId).setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, com.example.chefsrecipe.LoginScreenActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
