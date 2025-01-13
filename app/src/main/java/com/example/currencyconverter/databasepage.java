package com.example.currencyconverter;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.currencyconverter.database.BudgetDB;

public class databasepage extends AppCompatActivity {

    private EditText descriptionText, amountText;
    private Spinner creditDebit;
    private Button updateBtn, deleteBtn, goToCryptoBtn, goToNewsBtn, goToMainBtn;
    private ListView listView;
    private TextView totalDisplay;
    private BudgetDB dbHelper;
    private String selectedCreditDebit = "Credit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_databasepage);


        descriptionText = findViewById(R.id.budget_desc);
        amountText = findViewById(R.id.budget_amount);
        creditDebit = findViewById(R.id.credit_debit_dropdown);
        updateBtn = findViewById(R.id.update_btn);
        deleteBtn = findViewById(R.id.delete_btn);
        goToCryptoBtn = findViewById(R.id.go_to_crypto);
        goToNewsBtn = findViewById(R.id.go_to_news);
        goToMainBtn = findViewById(R.id.go_to_main);
        listView = findViewById(R.id.budget_list);
        totalDisplay = findViewById(R.id.total_amount);
        dbHelper = new BudgetDB(this);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.transaction_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        creditDebit.setAdapter(adapter);

        creditDebit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCreditDebit = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCreditDebit = "Credit";
            }
        });


        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEntry();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEntry();
            }
        });

        goToCryptoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(databasepage.this, cryptopage.class));
            }
        });

        goToNewsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(databasepage.this, newspage.class));
            }
        });

        goToMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(databasepage.this, MainActivity.class));
            }
        });


        displayData();
        updateTotalAmount();
    }


    //SQlite DB connection - https://www.geeksforgeeks.org/how-to-create-and-add-data-to-sqlite-database-in-android/ - A.A.Rifath (SA23089754)
    //https://www.geeksforgeeks.org/how-to-view-and-locate-sqlite-database-in-android-studio/ - A.A.Rifath (SA23089754)

    private void addEntry() {
        String description = descriptionText.getText().toString();
        String amountStr = amountText.getText().toString();

        if (description.isEmpty() || amountStr.isEmpty()) {
            showPopup("Error", "Please enter all fields.");
        } else {
            double amount = Double.parseDouble(amountStr);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            if (selectedCreditDebit.equals("Debit")) {
                double currentTotal = getTotalAmount();

                if (currentTotal == 0) {
                    showPopup("Error", "Cannot debit before credit.");
                    return;
                } else if (amount > currentTotal) {
                    showPopup("Error", "Debit amount greater than total.");
                    return;
                }

                amount = -amount;
            }

            ContentValues values = new ContentValues();
            values.put(BudgetDB.COLUMN_DESC, description);
            values.put(BudgetDB.COLUMN_AMOUNT, amount);

            db.insert(BudgetDB.TABLE_NAME, null, values);

            descriptionText.setText("");
            amountText.setText("");

            displayData();
            updateTotalAmount();
        }
    }


    private void deleteEntry() {
        String stringID = descriptionText.getText().toString();

        if (stringID.isEmpty()) {
            showPopup("Error", "Please enter ID in description field");
        } else {
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            int rowDeleted = db.delete(BudgetDB.TABLE_NAME,
                    BudgetDB.COLUMN_ID + "=?",
                    new String[]{stringID});

            if (rowDeleted > 0) {
                showPopup("Success", "Entry deleted successfully.");
                descriptionText.setText("");
                displayData();
                updateTotalAmount();
            } else {
                showPopup("Error", "Entry not found.");
            }
        }
    }


    private double getTotalAmount() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + BudgetDB.COLUMN_AMOUNT + ") FROM " + BudgetDB.TABLE_NAME, null);

        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }


    private void updateTotalAmount() {
        double total = getTotalAmount();
        totalDisplay.setText("Account balance: LKR " + total);
    }


    //YT vid for popup (https://youtu.be/MM55ERxUI-Q?si=3fKh89eiwRPANmx4) - A.A.Rifath (SA23089754)
    private void showPopup(String title, String message) {
        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.custom_popup);

        TextView titleView = dialog.findViewById(R.id.popup_title);
        TextView messageView = dialog.findViewById(R.id.popup_msg);
        Button closeButton = dialog.findViewById(R.id.popup_ok);

        titleView.setText(title);
        messageView.setText(message);

        closeButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }


    private void displayData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(BudgetDB.TABLE_NAME, null, null, null, null, null, null);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.db_item,
                cursor,
                new String[]{BudgetDB.COLUMN_ID, BudgetDB.COLUMN_DESC, BudgetDB.COLUMN_AMOUNT},
                new int[]{R.id.item_id, R.id.item_desc, R.id.item_amount},
                0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.item_amount) {
                    double amount = cursor.getDouble(columnIndex);
                    ((TextView) view).setText("LKR " + amount);
                    return true;
                }
                return false;
            }
        });

        listView.setAdapter(adapter);
    }
}
