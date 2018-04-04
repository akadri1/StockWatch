package com.akshathakadri.stockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by akshathakadri on 2/22/18.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    ///DB Columns
    private static final String DATABASE_NAME = "StockAppDB";
    private static final String TABLE_NAME = "StockWatchTable";
    private static final String SYMBOL = "StockSymbol";
    private static final String COMPANY = "CompanyName";
    private static final String PRICE = "Price";
    private static final String CHANGE = "Change";
    private static final String PERCENT = "Percent";

    // DB Table Create Code
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE "+TABLE_NAME+" (" +
                    SYMBOL+" TEXT not null unique," +
                    COMPANY+" TEXT not null," +
                    PRICE+" TEXT not null," +
                    CHANGE+" TEXT not null," +
                    PERCENT+" TEXT not null" +
                    ")";

    private SQLiteDatabase database;


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getWritableDatabase(); // Inherited from SQLiteOpenHelper
        Log.d(TAG, "DatabaseHandler: DONE");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // onCreate is only called is the DB does not exist
        Log.d(TAG, "onCreate: Making New DB");
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public ArrayList<Stock> loadStocks() {

        Log.d(TAG, "loadStocks: START");
        ArrayList<Stock> stocks = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME, // The table to query
                new String[]{ SYMBOL, COMPANY, PRICE, CHANGE, PERCENT }, // The columns to return
                null, // The columns for the WHERE clause, null means “*” null, // The values for the WHERE clause, null means “*” null, // don't group the rows
                null, // don't filter by row groups
                null,
                null,
                null); // The sort order
        if (cursor != null) { // Only proceed if cursor is not null
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0); // 1st returned column
                String company = cursor.getString(1); // 2nd

                String price = cursor.getString(2);
                String change = cursor.getString(3);
                String percent = cursor.getString(4);

                Stock stock = new Stock(symbol,company,Double.parseDouble(price),Double.parseDouble(change),Double.parseDouble(percent));
                stocks.add(stock);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return stocks;
    }

    public void addStock(Stock stock) {
        Log.d(TAG, "addStock: Adding " + stock.getSymbol());
        ContentValues values = new ContentValues();
        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY, stock.getCompany());
        values.put(PRICE, stock.getLatestPrice());
        values.put(CHANGE, stock.getChange());
        values.put(PERCENT, stock.getPercent());
        database.insert(TABLE_NAME, "-1", values);
        Log.d(TAG, "addStock: Add Complete");
    }

    /*public void updateStock(Stock stock) {
        Log.d(TAG, "updateStock: START");
        ContentValues values = new ContentValues();

        values.put(SYMBOL, stock.getSymbol());
        values.put(COMPANY, stock.getCompany());

        long key = database.update(TABLE_NAME, values, SYMBOL + " = ?", new String[]{stock.getSymbol()});

        Log.d(TAG, "updateStock: "+key);
    }*/

    public void deleteStock(String symbol) {
        Log.d(TAG, "deleteStock: Deleting Stock " + symbol);

        int cnt = database.delete(TABLE_NAME, SYMBOL+" = ?", new String[] { symbol });

        Log.d(TAG, "deleteStock: "+cnt);
    }

    public void dumpDbToLog() {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME, null);
        if (cursor != null) {
            cursor.moveToFirst();

            Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            for (int i = 0; i < cursor.getCount(); i++) {
                String symbol = cursor.getString(0);
                String company = cursor.getString(1);
                Log.d(TAG, "dumpDbToLog: " +
                        String.format("%s %-18s", symbol + ":", symbol) +
                        String.format("%s %-18s", company + ":", company));
                cursor.moveToNext();
            }
            cursor.close();
        }

        Log.d(TAG, "dumpDbToLog: ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
    }

    public void shutDown() {
        database.close();
    }


}
