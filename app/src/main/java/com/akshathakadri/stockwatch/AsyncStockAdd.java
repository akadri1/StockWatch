package com.akshathakadri.stockwatch;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by akshathakadri on 2/23/18.
 */

public class AsyncStockAdd extends AsyncTask<String, Integer, Stock> {

    private static final String TAG = "AsyncStockAdd";

    public AsyncAddResponse responseHandler = null;

    private static final String STOCK_URL = "https://api.iextrading.com/1.0/stock/";
    private static final String STOCK_URL_SUFFIX = "/quote";

    public AsyncStockAdd(AsyncAddResponse responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    protected void onPreExecute() {
        //Nothing here
    }

    @Override
    protected Stock doInBackground(String... params) {

        if(params == null || params.length <1) {
            return null;
        }

        String symbol = params[0];
        Stock stock = getStock(symbol);
        //mainActivity.addDone(symbol, stock);
        Log.d(TAG, "doInBackground complete: " );
        return stock;
    }

    @Override
    protected void onPostExecute(Stock s) {
        responseHandler.processAddFinish(s);
    }

    private Stock getStock(String symbol) {
        //Call http://d.yimg.com/aq/autoc?region=US&lang=en-US&query=CAI
        String urlToUse = STOCK_URL+symbol.trim()+STOCK_URL_SUFFIX;
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return parseStockJSON(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Stock parseStockJSON(String s) {
        try {
            JSONObject jObjMain = new JSONObject(s);

            String symbol = jObjMain.getString("symbol");
            String name = jObjMain.getString("companyName");
            String price = jObjMain.getString("latestPrice");
            String change = jObjMain.getString("change");
            String percent = jObjMain.getString("changePercent");
            return new Stock(symbol, name, Double.parseDouble(price),Double.parseDouble(change),Double.parseDouble(percent));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

