package com.example.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;
import com.example.currencyconverter.adapter.NewsAdapter;
import com.example.currencyconverter.model.NewsItem;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class newspage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private List<NewsItem> newsList;
    private Button goToMainBtn, goToDatabaseBtn, goToCryptoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newspage);


        recyclerView = findViewById(R.id.news_recycler);
        progressBar = findViewById(R.id.progress_bar);
        goToMainBtn = findViewById(R.id.go_to_main);
        goToDatabaseBtn = findViewById(R.id.go_to_db);
        goToCryptoBtn = findViewById(R.id.go_to_crypto);


        newsList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        newsAdapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(newsAdapter);

        searchNews();


        goToMainBtn.setOnClickListener(v -> {
            Intent intent = new Intent(newspage.this, MainActivity.class);
            startActivity(intent);
        });

        goToDatabaseBtn.setOnClickListener(v -> {
            Intent intent = new Intent(newspage.this, databasepage.class);
            startActivity(intent);
        });

        goToCryptoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(newspage.this, cryptopage.class);
            startActivity(intent);
        });

    }


    //YT vid api connection (https://youtu.be/p7CJC6hb9NM?si=PdZ5FWVXHsYlbUje) - H.K.Dilanjan (SA23439450)
    private void searchNews() {
        progressBar.setVisibility(View.VISIBLE);


        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();

                String url = "https://newsapi.org/v2/everything?q=economy&apiKey=280cad1e358e4b0c8c7b808e0507e483";
                Request request = new Request.Builder().url(url).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray articles = jsonObject.getJSONArray("articles");


                    for (int i = 0; i < articles.length(); i++) {
                        JSONObject article = articles.getJSONObject(i);
                        String title = article.getString("title");
                        String description = article.getString("description");
                        String publishedAt = article.getString("publishedAt");


                        newsList.add(new NewsItem(title, description, publishedAt));
                    }


                    runOnUiThread(() -> {
                        newsAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    });

                } else {
                    showError("Failed to load news.");
                }

            } catch (Exception e) {
                runOnUiThread(() -> showError("Error: " + e.getMessage()));
            }

        }).start();
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(newspage.this, message, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        });
    }
}
