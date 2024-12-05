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

    //OkHttpClient is used to do HTTP calls, in this case, to call the API
    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey = "nghgU/xbrOsjWJHm8R/amA==78Hk0ZQil4itXi9X";

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

        // Define the API endpoint URL, including the query parameter
        String url = "https://api.calorieninjas.com/v1/nutrition?query=" + query;

        // Cria uma requisição HTTP usando OkHttp
        Request request = new Request.Builder()
                .url(url) // Set the URL for the request
                .addHeader("X-Api-Key", apiKey) // Add the API key in the request header
                .build(); // Build the request object

        // Make an asynchronous network call using the OkHttp client
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle the failure case and update the UI with an error message
                runOnUiThread(() -> resultTextView.setText("Request failed: " + e.getMessage()));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) { // Check if the HTTP response is successful (status code 200)
                    // Pega o corpo da resposta como String
                    String responseData = response.body().string();

                    // Cria um objeto GSON para passar uma resposta JSON
                    Gson gson = new Gson();

                    // Converte a resposta JSON  em um objeto JsonObject
                    JsonObject jsonResponse = gson.fromJson(responseData, JsonObject.class);

                    // Extract the "items" array from the JSON response
                    JsonArray items = jsonResponse.getAsJsonArray("items");

                    // Constrói uma string formatada para exibição
                    StringBuilder formattedResponse = new StringBuilder();
                    //this for loop is here in case I want to look for more than one item
                    for (int i = 0; i < items.size(); i++) {
                        NutritionItem item = gson.fromJson(items.get(i), NutritionItem.class);
                        formattedResponse.append("Item name: ").append(item.getName()).append("\n")
                                .append("Item Calories: ").append(item.getCalories()).append("\n")
                                .append("Protein (g): ").append(item.getProtein_g()).append("\n")
                                .append("Total Fat (g): ").append(item.getFat_total_g()).append("\n")
                                .append("Total Carbs (g): ").append(item.getCarbohydrates_total_g()).append("\n\n");
                    }

                    //Fetch data from API then update textView with the results
                    runOnUiThread(() -> resultTextView.setText(formattedResponse.toString()));
                } else {
                    runOnUiThread(() -> resultTextView.setText("Request failed: " + response.message()));
                }
            }
        });
    }
}