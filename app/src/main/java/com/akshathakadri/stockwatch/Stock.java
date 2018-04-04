package com.akshathakadri.stockwatch;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by akshathakadri on 2/23/18.
 */

public class Stock implements Serializable, Comparable<Stock>{
    private String symbol;
    private String company;
    private double latestPrice;
    private double change;
    private double percent;

    public Stock(String symbol, String company) {
        this.symbol = symbol;
        this.company = company;
    }

    public Stock(String symbol, String company, double latestPrice, double change, double percent) {
        this.symbol = symbol;
        this.company = company;
        this.latestPrice = latestPrice;
        this.change = change;
        this.percent = percent;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(double latestPrice) {
        this.latestPrice = latestPrice;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    @Override
    public int compareTo(@NonNull Stock stock) {
        return this.symbol.compareTo(stock.getSymbol());
    }
}
