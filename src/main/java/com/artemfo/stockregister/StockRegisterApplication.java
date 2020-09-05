package com.artemfo.stockregister;

import com.artemfo.stockregister.entity.Stock;
import com.artemfo.stockregister.service.StockService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootApplication
@Slf4j
public class StockRegisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockRegisterApplication.class, args);
    }

    @Bean
    @Profile("!test")
    CommandLineRunner runner(StockService stockService) {
        return (args -> {
            if (stockService.findAll(null, "", 1, 10).isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                TypeReference<List<Stock>> typeReference = new TypeReference<List<Stock>>() {
                };
                InputStream inputStream = TypeReference.class.getResourceAsStream("/json/stocks.json");
                try {
                    List<Stock> stockList = mapper.readValue(inputStream, typeReference);
                    stockService.saveAll(stockList);
                    log.info("Stocks saved");
                } catch (IOException e) {
                    System.out.println("Unable to save stocks: " + e.getMessage());
                }
            }
        });
    }
}
