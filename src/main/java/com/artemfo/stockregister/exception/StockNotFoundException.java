package com.artemfo.stockregister.exception;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(Long id) {
        super("Could not find stock " + id);
    }
}