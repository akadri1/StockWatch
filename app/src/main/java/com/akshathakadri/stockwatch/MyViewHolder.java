package com.akshathakadri.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by akshathakadri on 2/23/18.
 */

public class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView symbol;
    public TextView company;
    public TextView price;
    public TextView indicator;
    public TextView details;

    public MyViewHolder(View view) {
        super(view);
        symbol = (TextView) view.findViewById(R.id.symbol);
        company = (TextView) view.findViewById(R.id.company);
        price = (TextView) view.findViewById(R.id.price);
        details = (TextView) view.findViewById(R.id.details);
        indicator = (TextView) view.findViewById(R.id.indicator);
    }

}
