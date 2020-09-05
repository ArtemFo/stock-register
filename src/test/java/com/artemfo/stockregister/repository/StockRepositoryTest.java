package com.artemfo.stockregister.repository;

import com.artemfo.stockregister.entity.Stock;
import com.artemfo.stockregister.entity.StockStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static com.artemfo.stockregister.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
@Slf4j
class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    @Test
    void repoIsNotNull() {
        assertThat(stockRepository).isNotNull();
    }

    @Test
    void save() {
        Stock stock = stockRepository.save(ST_1);
        assertThat(stock).isEqualTo(ST_1);
    }

    @Test
    void save_withoutComment() {
        Stock stock = stockRepository.save(ST_NO_COMMENT);
        assertThat(stock.getComment()).isEqualTo("");
    }

    @Test
    void findAll() {
        stockRepository.save(ST_1);
        stockRepository.save(ST_2);
        stockRepository.save(ST_3);
        List<Stock> stockList = stockRepository.findAll();
        assertThat(stockList).isEqualTo(STOCK_LIST);
    }

    @Test
    void update() {
        stockRepository.save(ST_1);
        stockRepository.save(ST_2_ID_1);
        assertThat(stockRepository.findById(1L).get()).isEqualTo(ST_2_ID_1);
    }

    @Test
    void findAllByStatusAndCodeContains_allParams() {
        stockRepository.save(ST_1);
        stockRepository.save(ST_2);
        stockRepository.save(ST_3);
        Pageable pageable = PageRequest.of(0, 10);
        List<Stock> stockList = stockRepository
                .findAllByStatusAndCodeContains(StockStatus.ACTIVE, "123", pageable)
                .getContent();
        assertThat(stockList).isEqualTo(STOCK_LIST_1_3);
    }

    @Test
    void findAllByStatusAndCodeContains_status() {
        stockRepository.save(ST_1);
        stockRepository.save(ST_2);
        stockRepository.save(ST_DELETED);
        Pageable pageable = PageRequest.of(0, 10);
        List<Stock> stockList = stockRepository
                .findAllByStatusAndCodeContains(StockStatus.DELETED, "", pageable)
                .getContent();
        assertThat(stockList.get(0)).isEqualTo(ST_DELETED);
    }

    @Test
    void findAllByCodeContains() {
        stockRepository.save(ST_1);
        stockRepository.save(ST_2);
        stockRepository.save(ST_DELETED);
        Pageable pageable = PageRequest.of(0, 10);
        List<Stock> stockList = stockRepository
                .findAllByCodeContains("", pageable)
                .getContent();
        assertThat(stockList).isEqualTo(STOCK_LIST_WITH_DELETED);
    }
}