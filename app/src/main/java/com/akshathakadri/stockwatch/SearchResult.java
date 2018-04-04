package com.akshathakadri.stockwatch;

import java.util.List;

/**
 * Created by akshathakadri on 2/25/18.
 */

public class SearchResult {
    private List<String> searchResults;
    private String symbol;

    public List<String> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<String> searchResults) {
        this.searchResults = searchResults;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
