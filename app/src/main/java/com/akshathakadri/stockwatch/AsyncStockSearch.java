package com.akshathakadri.stockwatch;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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

public class AsyncStockSearch extends AsyncTask<String, Integer, SearchResult> {

    private static final String TAG = "AsyncStockLoader";
    public AsyncSearchResponse responseHandler = null;

    private static final String SEARCH_URL = "http://d.yimg.com/aq/autoc?region=US&lang=en-US&query=";

    public AsyncStockSearch (AsyncSearchResponse responseHandler) {
        this.responseHandler = responseHandler;
    }
    @Override
    protected void onPreExecute() {
        //Nothing here
    }

    @Override
    protected SearchResult doInBackground(String... params) {
        if(params == null || params.length <1) {
            return null;
        }
        String symbol = params[0];
        SearchResult searchResult = new SearchResult();
        searchResult.setSymbol(symbol);
        List<String> strs = searchStock(symbol);
        searchResult.setSearchResults(strs);
        //mainActivity.searchDone(symbol, stockList);
        Log.d(TAG, "doInBackground: " );

        return searchResult;
    }

    @Override
    protected void onPostExecute(SearchResult s) {
        responseHandler.processSearchFinish(s);
    }

    private List<String> searchStock(String query) {
        String urlToUse = SEARCH_URL+query;
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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parseJSON(sb.toString());
    }

    private List<String> parseJSON(String s) {
        try {
            JSONObject jObjMain = new JSONObject(s);
            JSONObject jResultSet = jObjMain.getJSONObject("ResultSet");
            JSONArray stocks = jResultSet.getJSONArray("Result");

            List<String> stockListTemp = new ArrayList<String>();

            for(int i=0; i < stocks.length(); i++) {
                JSONObject jstock = (JSONObject) stocks.get(i);
                //String exchange = jstock.getString("exchDisp");
                //if(exch.equals("NASDAQ")|| exch.equals("NAS") || exch.equals("NYSE")) {
                String symbol = jstock.getString("symbol");
                String type = jstock.getString("type");
                if(type.equals("S")&&!symbol.contains(".")) {
                    String name = jstock.getString("name");
                    String ch = symbol+MainActivity.DASH+name;
                    stockListTemp.add(ch);
                }
            }
            return stockListTemp;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

