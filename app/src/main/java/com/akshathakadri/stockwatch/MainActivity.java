package com.akshathakadri.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, View.OnLongClickListener,
            AsyncResponse, AsyncAddResponse, AsyncSearchResponse {

    private static final String TAG = "MainActivity";
    private List<Stock> stockList = new ArrayList<>();  // Main content is here
    private String symbol;

    private DatabaseHandler databaseHandler;
    private RecyclerView recyclerView; // Layout's recyclerview
    private StockAdapter mAdapter; // Data to recyclerview adapter

    private SwipeRefreshLayout swiper;
    public static final String DASH = " - ";
    public static final String market = "http://www.marketwatch.com/investing/stock/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);

        mAdapter = new StockAdapter(stockList, this);

        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseHandler = new DatabaseHandler(this);

        swiper = findViewById(R.id.swiper);

        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(doNetCheck()) {
                    refreshData();
                } else {
                    swiper.setRefreshing(false);
                }
            }
        });
    }


    @Override
    protected void onResume() {
        if(doNetCheck()) {
            refreshData();
            Log.d(TAG, "onResume: ");
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        databaseHandler.shutDown();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addstock, menu);
        return true;
    }

    @Override
    public void onClick(View v) {  // click listener called by ViewHolder clicks
        if(doNetCheck()) {
            int pos = recyclerView.getChildLayoutPosition(v);
            Stock c = stockList.get(pos);
            String url = market+c.getSymbol();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }

    @Override
    public boolean onLongClick(final View v) {  // long click listener called by ViewHolder long clicks
        if(doNetCheck()) {
            final int pos = recyclerView.getChildLayoutPosition(v);
            final Stock m = stockList.get(pos);

            //Delete note here
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    databaseHandler.deleteStock(m.getSymbol());
                    stockList.remove(pos);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(v.getContext(), "Stock '" + m.getSymbol() + "' deleted", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Nothing
                }
            });

            builder.setIcon(R.drawable.ic_delete);
            builder.setMessage("Delete Stock '" + m.getSymbol() + "' ?");
            builder.setTitle("Delete confirmation.");

            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return false;
    }

   //Menu item - new Stock
    public void searchStock() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final EditText et = new EditText(this);

        InputFilter[] editFilters = et.getFilters();
        InputFilter[] newFilters = new InputFilter[editFilters.length + 1];
        System.arraycopy(editFilters, 0, newFilters, 0, editFilters.length);
        newFilters[editFilters.length] = new InputFilter.AllCaps();
        et.setFilters(newFilters);

        et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        et.setGravity(Gravity.CENTER_HORIZONTAL);

        builder.setView(et);

        final AsyncStockSearch asyncTask = new AsyncStockSearch(this);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Search stock
                symbol =et.getText().toString();
                asyncTask.execute(symbol);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.setMessage("Please enter a Stock Symbol:");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void stockListDialogue(SearchResult searchResult) {
        if (searchResult == null)
            return;

        final List<String> matchStock = searchResult.getSearchResults();
        String searchSymbol = searchResult.getSymbol();
        final AsyncStockAdd asyncTask = new AsyncStockAdd(this);
        if((matchStock == null) || (matchStock.size() <= 0)) {
            noButtonDialogue(getString(R.string.symbol_not_found) + searchSymbol, getString(R.string.symbol_not_found_msg), -1);
        } else {
            if (matchStock.size() == 1) {
                if(matchStock.get(0) == null) {
                    noButtonDialogue(getString(R.string.symbol_not_found) + searchSymbol, getString(R.string.symbol_not_found_msg), -1);
                    return;
                }
                if(isExist(searchSymbol)) {
                    noButtonDialogue("Duplicate Stock", "Stock symbol "+symbol+" already displayed", R.drawable.ic_report);
                } else {
                    symbol = getSymbolFromSeq(matchStock.get(0));
                    asyncTask.execute(symbol);
                }
            } else {
                // List selection dialog
                final CharSequence[] cs = matchStock.toArray(new CharSequence[matchStock.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Make a selection");
                builder.setItems(cs, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        symbol = getSymbolFromSeq(cs[which]);

                        if(isExist(symbol)) {
                            noButtonDialogue("Duplicate Stock", "Stock symbol "+symbol+" already displayed", R.drawable.ic_report);
                        } else {
                            asyncTask.execute(symbol);
                        }
                    }
                });
                builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    private boolean isExist(String sym) {
        boolean exists =false;
        for(Stock stock:stockList) {
            if(stock.getSymbol().equals(sym)){
                exists =true;
                break;
            }
        }
        return exists;
    }

    private String getSymbolFromSeq(CharSequence seq) {
        return seq.toString().split(DASH)[0].trim();
    }

    public void noButtonDialogue(String title, String message, int icon) {
        // noSymbolDialogue dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (icon!=-1)
            builder.setIcon(icon);

        builder.setTitle(title);
        builder.setMessage(message);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean doNetCheck(){
        if(isConnected()) {
            //Toast.makeText(this, R.string.connected, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            noButtonDialogue(getString(R.string.no_net_title),getString(R.string.no_net_msg), -1);
            return false;
        }
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager==null) return false;
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        return (info !=null && info.isConnectedOrConnecting());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addStock:
                Toast.makeText(this, "Adding new Stock..", Toast.LENGTH_SHORT).show();
                if(doNetCheck()) {
                    searchStock();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshData() {
        stockList.clear();
        databaseHandler.dumpDbToLog();
        ArrayList<Stock> list = databaseHandler.loadStocks();
        stockList.addAll(list);
        AsyncStockLoader asyncTask = new AsyncStockLoader(this);
        asyncTask.responseHandler = this;
        Stock[] stocks = stockList.toArray(new Stock[stockList.size()]);
        asyncTask.execute(stocks);
    }
    /*
     * Async tasks
     */
    @Override
    public void processFinish(List<Stock> list) {
        swiper.setRefreshing(false);
        if(stockList.size() != list.size()) {
            noButtonDialogue("ERROR!","Error loading few stocks, please try later", -1);
        } else {
            stockList.clear();
            stockList.addAll(list);
            Collections.sort(stockList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void processAddFinish(Stock stock) {
        if(stock==null || symbol==null || !symbol.equals(stock.getSymbol())) {
            Toast.makeText(this, R.string.error_sym_msg, Toast.LENGTH_SHORT).show();
            return;
        }
        databaseHandler.addStock(stock);
        stockList.add(stock);
        Collections.sort(stockList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void processSearchFinish(SearchResult searchResult) {
        stockListDialogue(searchResult);
    }

}