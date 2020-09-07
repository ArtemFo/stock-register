package com.artemfo.stockregister.service;

import com.artemfo.stockregister.entity.Stock;
import com.artemfo.stockregister.entity.StockStatus;
import com.artemfo.stockregister.exception.StockAlreadyDeletedException;
import com.artemfo.stockregister.exception.StockAlreadyExistsException;
import com.artemfo.stockregister.exception.StockNotFoundException;
import com.artemfo.stockregister.repository.StockRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
@Slf4j
public class StockService {

    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Stock create(Stock stock) {
        Assert.notNull(stock, "Stock must not be null");
        log.info("Create {}", stock.toString());
        stock.setStatus(StockStatus.ACTIVE);
        try {
            return stockRepository.save(stock);
        } catch (DataIntegrityViolationException ex) {
            throw new StockAlreadyExistsException(stock);
        }
    }

    public Stock getById(Long id) {
        log.info("Find by id {}", id);
        return stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException(id));
    }

    public Stock update(Stock newStock, Long id) {
        Assert.notNull(newStock, "Stock must not be null");
        log.info("Update {}", newStock.toString());
        Stock oldStock = getById(id);
        newStock.setId(oldStock.getId());
        newStock.setCode(oldStock.getCode());
        return stockRepository.save(newStock);
    }

    public Stock delete(Long id) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException(id));
        if (stock.getStatus() != null && stock.getStatus().equals(StockStatus.DELETED)) {
            throw new StockAlreadyDeletedException(id);
        }
        stock.setStatus(StockStatus.DELETED);
        return stockRepository.save(stock);
    }

    public Page<Stock> findAll(StockStatus status, String code, int pageNum, int pageSize) {
        code = code == null ? "" : code;
        pageNum = pageNum < 1 ? 0 : pageNum - 1;
        pageSize = Math.max(pageSize, 10);
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        if (status == null) {
            return stockRepository.findAllByCodeContains(code, pageable);
        }
        return stockRepository.findAllByStatusAndCodeContains(status, code, pageable);
    }

    public void saveAll(List<Stock> stockList) {
        log.info("Save all");
        stockList.forEach(s -> {
            s.setStatus(StockStatus.ACTIVE);
            stockRepository.save(s);
        });
    }
}
