package com.artemfo.stockregister.controller.advice;

import com.artemfo.stockregister.exception.StockAlreadyDeletedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class StockAlreadyDeletedAdvice {

    @ResponseBody
    @ExceptionHandler(StockAlreadyDeletedException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String stockAlreadyDeletedHandler(StockAlreadyDeletedException ex) {
        return ex.getMessage();
    }
}
