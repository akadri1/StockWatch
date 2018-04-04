package com.akshathakadri.stockwatch;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by akshathakadri on 2/23/18.
 */

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainAct;
    private DecimalFormat df = new DecimalFormat("#0.00");

    public StockAdapter(List<Stock> empList, MainActivity ma) {
            this.stockList = empList;
            mainAct = ma;
        }

        @Override
        public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder: MAKING NEW");
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_stock, parent, false);
            itemView.setOnClickListener(mainAct);
            itemView.setOnLongClickListener(mainAct);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Stock stock = stockList.get(position);
            holder.symbol.setText(stock.getSymbol());
            holder.company.setText(stock.getCompany());
            holder.price.setText(stock.getLatestPrice()+"");


            if(stock.getChange()>0){
                holder.indicator.setText(mainAct.getString(R.string.up_arrow));
                holder.symbol.setTextColor(Color.GREEN);
                holder.indicator.setTextColor(Color.GREEN);
                holder.company.setTextColor(Color.GREEN);
                holder.price.setTextColor(Color.GREEN);
                holder.details.setTextColor(Color.GREEN);
            } else {
                holder.indicator.setText(mainAct.getString(R.string.down_arrow));
                holder.symbol.setTextColor(Color.RED);
                holder.indicator.setTextColor(Color.RED);
                holder.company.setTextColor(Color.RED);
                holder.price.setTextColor(Color.RED);
                holder.details.setTextColor(Color.RED);
            }

            StringBuilder details = new StringBuilder();
            details.append(df.format(stock.getChange()));
            details.append("(");
            details.append(df.format(stock.getPercent()));
            details.append("%)");

            holder.details.setText(details.toString());
        }

        @Override
        public int getItemCount() {
            return stockList.size();
        }

    
}
