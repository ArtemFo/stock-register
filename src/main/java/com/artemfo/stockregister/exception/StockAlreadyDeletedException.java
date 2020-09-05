package com.artemfo.stockregister.exception;

public class StockAlreadyDeletedException extends RuntimeException {
    public StockAlreadyDeletedException(Long id) {
        super("Already deleted stock " + id);
    }
}
