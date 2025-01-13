package com.example.currencyconverter.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//SQlite DB connection - https://www.geeksforgeeks.org/how-to-create-and-add-data-to-sqlite-database-in-android/ - A.A.Rifath (SA23089754)
//https://www.geeksforgeeks.org/how-to-view-and-locate-sqlite-database-in-android-studio/ - A.A.Rifath (SA23089754)

public class BudgetDB extends SQLiteOpenHelper {


    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "budget_tracker.db";


    public static final String TABLE_NAME = "budget";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_AMOUNT = "amount";


    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DESC + " TEXT, " +
            COLUMN_AMOUNT + " REAL);";

    public BudgetDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
