package com.example.currencyconverter;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
`
import com.example.currencyconverter.adapter.CryptoAdapter;
import com.example.currencyconverter.model.CryptoItem;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class cryptopage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CryptoAdapter cryptoAdapter;
    private Button goToDatabase, goToMainBtn, goToNewsBtn;

    private static final String BASE_URL_LIVE = "https://api.coinlayer.com/api/live";
    private static final String BASE_URL_LIST = "https://api.coinlayer.com/api/list";
    private static final String API_KEY = "8c52eb453d72eac5917badbbe171cb9a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cryptopage);


        recyclerView = findViewById(R.id.crypto_recycler);
        progressBar = findViewById(R.id.progress_bar);
        goToDatabase = findViewById(R.id.go_to_db);
        goToMainBtn = findViewById(R.id.go_to_main);
        goToNewsBtn = findViewById(R.id.go_to_news);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        goToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(cryptopage.this, databasepage.class));
            }
        });

        goToMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(cryptopage.this, MainActivity.class));
            }
        });

        goToNewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(cryptopage.this, newspage.class));
            }
        });


        new GetCryptoData().execute();
    }

    private class GetCryptoData extends AsyncTask<Void, Void, List<CryptoItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }


        //https://www.geeksforgeeks.org/inputstreamreader-class-in-java/ - S.Shomiya (SA23439696)
        @Override
        protected List<CryptoItem> doInBackground(Void... voids) {
            List<CryptoItem> cryptoList = new ArrayList<>();
            try {

                //YT vid api connection (https://youtu.be/p7CJC6hb9NM?si=PdZ5FWVXHsYlbUje) - S.Shomiya (SA23439696)
                URL urlList = new URL(BASE_URL_LIST + "?access_key=" + API_KEY + "&target=LKR");
                HttpURLConnection connectionList = (HttpURLConnection) urlList.openConnection();
                connectionList.setRequestMethod("GET");
                connectionList.connect();

                BufferedReader readerList = new BufferedReader(new InputStreamReader(connectionList.getInputStream()));
                StringBuilder responseList = new StringBuilder();
                String lineList;

                while ((lineList = readerList.readLine()) != null) {
                    responseList.append(lineList);
                }

                JSONObject jsonListResponse = new JSONObject(responseList.toString());
                JSONObject cryptoNames = jsonListResponse.getJSONObject("crypto");


                URL urlLive = new URL(BASE_URL_LIVE + "?access_key=" + API_KEY + "&target=LKR");
                HttpURLConnection connectionLive = (HttpURLConnection) urlLive.openConnection();
                connectionLive.setRequestMethod("GET");
                connectionLive.connect();

                BufferedReader readerLive = new BufferedReader(new InputStreamReader(connectionLive.getInputStream()));
                StringBuilder responseLive = new StringBuilder();
                String lineLive;

                while ((lineLive = readerLive.readLine()) != null) {
                    responseLive.append(lineLive);
                }

                JSONObject jsonLiveResponse = new JSONObject(responseLive.toString());
                JSONObject rates = jsonLiveResponse.getJSONObject("rates");


                Iterator<String> keys = rates.keys();
                List<String> allCurrencies = new ArrayList<>();

                while (keys.hasNext()) {
                    allCurrencies.add(keys.next());
                }

                Random random = new Random();

                for (int i = 0; i < 50; i++) {
                    String randomCurrency = allCurrencies.get(random.nextInt(allCurrencies.size()));
                    double price = rates.getDouble(randomCurrency);
                    String name = cryptoNames.getJSONObject(randomCurrency).getString("name");
                    cryptoList.add(new CryptoItem(randomCurrency, name, price));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return cryptoList;
        }

        @Override
        protected void onPostExecute(List<CryptoItem> cryptoList) {
            super.onPostExecute(cryptoList);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (cryptoList.isEmpty()) {
                Toast.makeText(cryptopage.this, "Failed to get data.", Toast.LENGTH_SHORT).show();
            } else {
                cryptoAdapter = new CryptoAdapter(cryptoList);
                recyclerView.setAdapter(cryptoAdapter);
            }
        }
    }
}
