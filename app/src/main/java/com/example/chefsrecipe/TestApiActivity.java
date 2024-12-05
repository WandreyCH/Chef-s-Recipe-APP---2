package com.example.chefsrecipe;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TestApiActivity extends AppCompatActivity {

    // Declaração de variáveis
    private EditText editTextQuery1, editTextQuery2, editTextQuery3;
    private TextView resultText1, resultText2, resultText3;
    private Button buttonQuery1, buttonQuery2, buttonQuery3;

    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey = "nghgU/xbrOsjWJHm8R/amA==78Hk0ZQil4itXi9X"; // Substitua pela sua chave

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_api_nutrition);

        // Inicializa os componentes da interface
        editTextQuery1 = findViewById(R.id.editTextQuery1);
        editTextQuery2 = findViewById(R.id.editTextQuery2);

        resultText1 = findViewById(R.id.resultText1);
        resultText2 = findViewById(R.id.resultText2);

        buttonQuery1 = findViewById(R.id.buttonQuery1);
        buttonQuery2 = findViewById(R.id.buttonQuery2);


        // Configura listeners para os botões
        buttonQuery1.setOnClickListener(v -> fetchNutritionData(editTextQuery1.getText().toString(), resultText1));
        buttonQuery2.setOnClickListener(v -> fetchNutritionData(editTextQuery2.getText().toString(), resultText2));
    }

    // Método para buscar dados da API
    private void fetchNutritionData(String query, TextView resultTextView) {
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a query", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "https://api.calorieninjas.com/v1/nutrition?query=" + query;

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Api-Key", apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> resultTextView.setText("Request failed: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    Gson gson = new Gson();

                    // Converte o JSON em um array de NutritionItem
                    JsonObject jsonResponse = gson.fromJson(responseData, JsonObject.class);
                    JsonArray items = jsonResponse.getAsJsonArray("items");

                    // Constrói uma string formatada para exibição
                    StringBuilder formattedResponse = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        NutritionItem item = gson.fromJson(items.get(i), NutritionItem.class);
                        formattedResponse.append("Item name: ").append(item.getName()).append("\n")
                                .append("Item Calories: ").append(item.getCalories()).append("\n")
                                .append("Protein (g): ").append(item.getProtein_g()).append("\n")
                                .append("Total Fat (g): ").append(item.getFat_total_g()).append("\n")
                                .append("Total Carbs (g): ").append(item.getCarbohydrates_total_g()).append("\n\n");
                    }

                    runOnUiThread(() -> resultTextView.setText(formattedResponse.toString()));
                } else {
                    runOnUiThread(() -> resultTextView.setText("Request failed: " + response.message()));
                }
            }
        });
    }
}