package com.artemfo.stockregister.controller.advice;

import com.artemfo.stockregister.exception.StockAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class StockAlreadyExistsAdvice {

    @ResponseBody
    @ExceptionHandler(StockAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String stockAlreadyExistsHandler(StockAlreadyExistsException ex) {
        return ex.getMessage();
    }
}