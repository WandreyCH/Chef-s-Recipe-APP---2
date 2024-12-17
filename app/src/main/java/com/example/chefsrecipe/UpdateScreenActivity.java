package com.example.chefsrecipe;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseUser;

public class UpdateScreenActivity extends AppCompatActivity {

    EditText editTextName, editTextPassword, confirmPassword, chefDescription;
    Button buttonUpdate;
    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_screen);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Inicializando as views
        editTextName = findViewById(R.id.editTextName);
        editTextPassword = findViewById(R.id.editTextPassword);
        confirmPassword = findViewById(R.id.cPassword);
        chefDescription = findViewById(R.id.chefDescription);
        buttonUpdate = findViewById(R.id.submitButton);

        buttonUpdate.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {
        String name = editTextName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPass = confirmPassword.getText().toString().trim();
        String description = chefDescription.getText().toString().trim();

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            // Validando os campos
            if (TextUtils.isEmpty(name) && TextUtils.isEmpty(password) && TextUtils.isEmpty(confirmPass) && TextUtils.isEmpty(description)) {
                Toast.makeText(UpdateScreenActivity.this, "No updates made", Toast.LENGTH_SHORT).show();
                return;
            }

            // Caso o nome não esteja vazio, atualiza no Firebase
            if (!TextUtils.isEmpty(name)) {
                databaseReference.child(userId).child("name").setValue(name);
            }

            // Caso a senha não esteja vazia e corresponda ao campo de confirmação de senha, atualiza
            if (!TextUtils.isEmpty(password) && password.equals(confirmPass)) {
                // Atualize a senha do Firebase Authentication
                firebaseUser.updatePassword(password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateScreenActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdateScreenActivity.this, "Password update failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (!TextUtils.isEmpty(password) && !password.equals(confirmPass)) {
                confirmPassword.setError("Passwords do not match");
                confirmPassword.requestFocus();
                return;
            }

            // Atualiza a descrição ou mantém, caso nao haja mudança.
            if (!TextUtils.isEmpty(description)) {
                databaseReference.child(userId).child("chefDescription").setValue(description);
            }

            // Mensagem de sucesso
            Toast.makeText(UpdateScreenActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            finish(); // Finaliza a activity após o update
        }
    }
}
