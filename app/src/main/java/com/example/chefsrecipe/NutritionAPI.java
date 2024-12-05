package com.example.chefsrecipe;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class NutritionAPI {

    private static final String API_KEY = "YOUR_API_KEY"; // Substitua pela sua chave API

    public static void getNutritionInfo(String query) {
        // URL da API
        String url = "https://api.calorieninjas.com/v1/nutrition?query=" + query;

        // Criando o cliente OkHttp
        OkHttpClient client = new OkHttpClient();

        // Criando a requisição
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Api-Key", API_KEY)
                .build();

        // Executando a requisição de forma assíncrona
        new Thread(() -> {
            try {
                // Enviando a requisição e obtendo a resposta
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    // Obtenha o corpo da resposta como uma string
                    String responseBody = response.body().string();

                    // Parse o corpo da resposta para JSON
                    JSONObject jsonResponse = new JSONObject(responseBody);
                    JSONArray items = jsonResponse.getJSONArray("items");

                    // Iterando pelos itens retornados
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        String name = item.getString("name");
                        double calories = item.getDouble("calories");
                        double protein = item.getDouble("protein_g");
                        double fat = item.getDouble("fat_total_g");
                        double carbohydrates = item.getDouble("carbohydrates_total_g");

                        // Exemplo de exibição de dados no Log
                        System.out.println("Item: " + name);
                        System.out.println("Calories: " + calories);
                        System.out.println("Protein: " + protein + "g");
                        System.out.println("Fat: " + fat + "g");
                        System.out.println("Carbohydrates: " + carbohydrates + "g");
                    }
                } else {
                    System.out.println("Request failed. Response code: " + response.code());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}