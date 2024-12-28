package com.example.currencyconverter;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.currencyconverter.database.ConversionDB;
import com.example.currencyconverter.adapter.ConversionAdapter;
import com.example.currencyconverter.model.ConversionModel;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText amountInput;
    private Spinner fromCurrencyDropdown, toCurrencyDropdown;
    private Button convertBtn, goToNewsBtn, goToDatabaseBtn, goToCryptoBtn;

    private ProgressBar progressBar;
    private RecyclerView recentConvertList;
    private ConversionAdapter adapter;
    private List<ConversionModel> recentConversions;
    private ConversionDB dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountInput = findViewById(R.id.amount_input);
        fromCurrencyDropdown = findViewById(R.id.from_currency_dropdown);
        toCurrencyDropdown = findViewById(R.id.to_currency_dropdown);
        convertBtn = findViewById(R.id.convert_button);

        progressBar = findViewById(R.id.progress_bar);
        goToNewsBtn = findViewById(R.id.go_to_news);
        goToDatabaseBtn = findViewById(R.id.go_to_db);
        goToCryptoBtn = findViewById(R.id.go_to_crypto);
        recentConvertList = findViewById(R.id.recent_converts);

        //YT vid for db connection (https://youtu.be/312RhjfetP8?si=AL9FwNOHjSzQd70Q) - A.A.Rifath (SA23089754)
        dbHelper = new ConversionDB(this);


        recentConversions = dbHelper.getAllConversions();


        adapter = new ConversionAdapter(recentConversions);
        recentConvertList.setLayoutManager(new LinearLayoutManager(this));
        recentConvertList.setAdapter(adapter);

        //YT vid for dropdown menu- https://youtu.be/jXSNobmB7u4?si=JGrq5c2XDhnAp74c - K.Nuha (SA23428492)
        String[] baseCurrency = {"Select base currency", "USD", "EUR", "GBP", "INR", "AUD", "CAD", "OMR", "LKR"};
        ArrayAdapter<String> baseAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, baseCurrency);
        baseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromCurrencyDropdown.setAdapter(baseAdapter);

        String[] targetCurrency = {"Select target currency", "USD", "EUR", "GBP", "INR", "AUD", "CAD", "OMR", "LKR"};
        ArrayAdapter<String> targetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, targetCurrency);
        targetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toCurrencyDropdown.setAdapter(targetAdapter);


        convertBtn.setOnClickListener(v -> performConversion());
        goToNewsBtn.setOnClickListener(v -> navigateTo(newspage.class));
        goToDatabaseBtn.setOnClickListener(v -> navigateTo(databasepage.class));
        goToCryptoBtn.setOnClickListener(v -> navigateTo(cryptopage.class));
    }

    private void performConversion() {
        String amountStr = amountInput.getText().toString().trim();
        String fromCurrency = fromCurrencyDropdown.getSelectedItem().toString();
        String toCurrency = toCurrencyDropdown.getSelectedItem().toString();

        if (fromCurrency.equals("Select base currency") || toCurrency.equals("Select target currency")) {
            showPopup("Error", "Please select currencies.");
            return;
        }

        if (amountStr.isEmpty()) {
            showPopup("Error", "Please enter amount.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            new CurrencyConvertTask(amount, fromCurrency, toCurrency).execute();
        } catch (NumberFormatException e) {
            showPopup("Error", "Invalid amount.");
        }
    }

    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(MainActivity.this, targetActivity);
        startActivity(intent);
    }

    private class CurrencyConvertTask extends AsyncTask<Void, Void, Double> {
        private final double amount;
        private final String fromCurrency, toCurrency;

        public CurrencyConvertTask(double amount, String fromCurrency, String toCurrency) {
            this.amount = amount;
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }


        //YT vid api connection (https://youtu.be/p7CJC6hb9NM?si=PdZ5FWVXHsYlbUje) - K.Nuha (SA23428492)
        @Override
        protected Double doInBackground(Void... voids) {
            try {
                String apiKey = "965eb42e88376b3afdc917a014f5c591";
                String apiUrl = "https://api.exchangerate-api.com/v4/latest/" + fromCurrency + "?apikey=" + apiKey;
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder json = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                reader.close();

                JSONObject response = new JSONObject(json.toString());
                JSONObject rates = response.getJSONObject("rates");
                return rates.getDouble(toCurrency);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Double rate) {
            super.onPostExecute(rate);
            progressBar.setVisibility(View.GONE);

            if (rate != null) {
                double convertedAmount = amount * rate;
                String result = String.format("%s %.2f", toCurrency, convertedAmount);


                ConversionModel conversion = new ConversionModel(amount, fromCurrency, convertedAmount, toCurrency);


                dbHelper.addConversion(conversion);


                recentConversions.add(0, conversion);
                adapter.notifyDataSetChanged();

                showPopup("Converted amount", result);
            } else {
                showPopup("Error", "Unable to convert. Please try again.");
            }
        }
    }


    //YT vid for popup (https://youtu.be/MM55ERxUI-Q?si=3fKh89eiwRPANmx4) - K.Nuha (SA23428492)
    private void showPopup(String title, String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomDialog);
        final View customView = getLayoutInflater().inflate(R.layout.custom_popup, null);

        TextView popupTitle = customView.findViewById(R.id.popup_title);
        TextView popupMsg = customView.findViewById(R.id.popup_msg);
        Button popupOk = customView.findViewById(R.id.popup_ok);

        popupTitle.setText(title);
        popupMsg.setText(message);

        builder.setView(customView);

        final AlertDialog dialog = builder.create();
        popupOk.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
