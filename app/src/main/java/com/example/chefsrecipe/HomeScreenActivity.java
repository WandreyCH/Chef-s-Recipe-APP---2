package com.example.chefsrecipe;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RatingBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
//    private RecyclerView recipeRecyclerView;

    EditText searchBar;
    TextView topRatedTitle;
    RatingBar ratingBar1;
    Button apiTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // Inicialize componentes (pode ser útil para lógica futura)
        searchBar = findViewById(R.id.searchBar);
        topRatedTitle = findViewById(R.id.topRatedTitle);
        ratingBar1 = findViewById(R.id.ratingBar1);
        apiTestButton = findViewById(R.id.ButtonApiTests);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        recipeRecyclerView = findViewById(R.id.recipeRecyclerView);
//        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiTestButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeScreenActivity.this, TestApiActivity.class);
            startActivity(intent);
        });

        // Simulando dados (serão substituídos por dados reais do backend futuramente)
        List<Recipe> topRatedRecipes = new ArrayList<>();
        topRatedRecipes.add(new Recipe("", "Pasta Carbonara", "aaaaa","bbbbb","ccccc"));
        topRatedRecipes.add(new Recipe("", "Sushi Deluxe", "aaaaa","bbbbb","ccccc"));
        topRatedRecipes.add(new Recipe("", "Chocolate Cake", "aaaaa","bbbbb","ccccc"));
        topRatedRecipes.add(new Recipe("", "Grilled Salmon", "aaaaa","bbbbb","ccccc"));

    } @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            navigateToProfile();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Navega para o perfil de Chef ou Home Cook, dependendo do usuário.
    private void navigateToProfile() {
        if (currentUser != null) {
            getRole(currentUser.getUid(), role -> {  // Passa o callback como argumento
                if ("Chef".equals(role)) {
                    Intent intent = new Intent(HomeScreenActivity.this, ChefProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeScreenActivity.this, HomeCookProfile.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void getRole(String userId, RoleCallback callback) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        reference.child("role").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String role = task.getResult().getValue(String.class);
                if (role != null) {
                    callback.onRoleReceived(role);  // Chama o callback com o valor do role
                } else {
                    callback.onRoleReceived("Home Cook");  // Valor padrão se nulo
                }
            } else {
                callback.onRoleReceived("Home Cook");  // Valor padrão em caso de erro
            }
        });
    }
    // Função que define o método onRoleReceived após "role" ser atualizado no firebase
    public interface RoleCallback {
        void onRoleReceived(String role);
    }
}