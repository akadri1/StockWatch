package com.akshathakadri.stockwatch;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshathakadri on 2/23/18.
 */

public class AsyncStockLoader extends AsyncTask<Stock, Integer, List<Stock>> {

    private static final String TAG = "AsyncStockLoader";

    private static final String STOCK_URL = "https://api.iextrading.com/1.0/stock/";
    private static final String STOCK_URL_SUFFIX = "/quote";

    public AsyncResponse responseHandler;

    public AsyncStockLoader(AsyncResponse responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    protected void onPreExecute() {
        //Toast.makeText(mainActivity, "AsyncTask onPreExecute", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected List<Stock> doInBackground(Stock... stocks) {
        if(stocks == null) {
            return null;
        }
        Log.d(TAG, "doInBackground: " );
        return loadDataFromNet(stocks);
    }

    @Override
    protected void onPostExecute(List<Stock> s) {
        responseHandler.processFinish(s);
    }

    private ArrayList<Stock> loadDataFromNet(Stock[] s) {

        ArrayList<Stock> stockList = new ArrayList<>();
        try {
            for (Stock stock : s) {
                stockList.add(getStock(stock.getSymbol()));
            }
        } catch (Exception e) {
            Log.d(TAG, "loadDataFromNet: Error:"+e.getMessage());
        }
        return stockList;
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

