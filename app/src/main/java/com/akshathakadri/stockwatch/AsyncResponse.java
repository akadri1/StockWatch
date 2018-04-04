package com.akshathakadri.stockwatch;

import java.util.List;

/**
 * Created by akshathakadri on 2/25/18.
 */

public interface AsyncResponse {

    void processFinish(List<Stock> stockList);

}
