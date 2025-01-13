package com.example.currencyconverter.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.currencyconverter.model.ConversionModel;
import java.util.ArrayList;
import java.util.List;

//SQlite DB connection - https://www.geeksforgeeks.org/how-to-create-and-add-data-to-sqlite-database-in-android/ - A.A.Rifath (SA23089754)
//https://www.geeksforgeeks.org/how-to-view-and-locate-sqlite-database-in-android-studio/ - A.A.Rifath (SA23089754)

public class ConversionDB extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "currency_converter.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "recent_conversions";
    private static final String COLUMN_ID = "id";
    private static final String ORIGINAL_AMOUNT = "original_amount";
    private static final String BASE_CURRENCY = "original_currency";
    private static final String CONVERTED_AMOUNT = "converted_amount";
    private static final String TARGET_CURRENCY = "target_currency";

    public ConversionDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ORIGINAL_AMOUNT + " REAL, "
                + BASE_CURRENCY + " TEXT, "
                + CONVERTED_AMOUNT + " REAL, "
                + TARGET_CURRENCY + " TEXT)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }


    public void addConversion(ConversionModel conversion) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ORIGINAL_AMOUNT, conversion.getOriginalAmount());
        values.put(BASE_CURRENCY, conversion.getOriginalCurrency());
        values.put(CONVERTED_AMOUNT, conversion.getConvertedAmount());
        values.put(TARGET_CURRENCY, conversion.getTargetCurrency());
        db.insert(TABLE_NAME, null, values);

        db.close();
    }


    public List<ConversionModel> getAllConversions() {

        List<ConversionModel> conversions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {

            do {
                double originalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(ORIGINAL_AMOUNT));
                String originalCurrency = cursor.getString(cursor.getColumnIndexOrThrow(BASE_CURRENCY));
                double convertedAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(CONVERTED_AMOUNT));
                String targetCurrency = cursor.getString(cursor.getColumnIndexOrThrow(TARGET_CURRENCY));
                conversions.add(new ConversionModel(originalAmount, originalCurrency, convertedAmount, targetCurrency));
            } while (cursor.moveToNext());

            cursor.close();
        }
        return conversions;
    }
}
