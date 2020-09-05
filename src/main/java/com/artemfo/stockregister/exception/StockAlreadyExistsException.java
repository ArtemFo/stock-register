package com.artemfo.stockregister.exception;

import com.artemfo.stockregister.entity.Stock;

public class StockAlreadyExistsException extends RuntimeException {
    public StockAlreadyExistsException(Stock stock) {
        super("Already exists stock " + stock);
    }
}
