package com.artemfo.stockregister.controller;

import com.artemfo.stockregister.entity.Stock;
import com.artemfo.stockregister.entity.StockStatus;
import com.artemfo.stockregister.service.StockService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    ResponseEntity<List<Stock>> findAll(@RequestParam(name = "status", required = false) StockStatus status,
                                        @RequestParam(name = "code", required = false) String code,
                                        @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
                                        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Page<Stock> page = stockService.findAll(status, code, pageNum, pageSize);
        HttpHeaders headers = new HttpHeaders();
        headers.add("currentPage", (page.getNumber() + 1) + "");
        headers.add("totalPages", page.getTotalPages() + "");
        headers.add("totalItems", page.getTotalElements() + "");

        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @PostMapping
    Stock createStock(@Valid @RequestBody Stock stock) {
        return stockService.create(stock);
    }

    @GetMapping("/{id}")
    Stock getById(@PathVariable Long id) {
        return stockService.getById(id);
    }

    @PutMapping("/{id}")
    void updateById(@Valid @RequestBody Stock stock) {
        stockService.update(stock);
    }

    @DeleteMapping("/{id}")
    void deleteById(@PathVariable Long id) {
        stockService.delete(id);
    }
}
