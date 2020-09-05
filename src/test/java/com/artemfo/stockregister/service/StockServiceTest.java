package com.artemfo.stockregister.service;

import com.artemfo.stockregister.entity.Stock;
import com.artemfo.stockregister.entity.StockStatus;
import com.artemfo.stockregister.exception.StockAlreadyDeletedException;
import com.artemfo.stockregister.exception.StockAlreadyExistsException;
import com.artemfo.stockregister.exception.StockNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.Collectors;

import static com.artemfo.stockregister.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class StockServiceTest {

    @Autowired
    private StockService service;

    @Test
    void create() {
        Stock stock = service.create(ST_1);
        assertThat(stock).isEqualTo(ST_1);
    }

    @Test
    void create_null() {
        assertThrows(IllegalArgumentException.class, () -> service.create(null));
    }

    @Test
    void create_exists() {
        service.create(ST_1);
        assertThrows(StockAlreadyExistsException.class, () -> service.create(ST_1_SAME_CODE));
    }

    @Test
    void getById() {
        service.create(ST_1);
        assertThat(service.getById(1L)).isEqualToIgnoringGivenFields(ST_1, "totalFaceValue");
    }

    @Test
    void getById_notFound() {
        assertThrows(StockNotFoundException.class, () -> service.getById(0L));
    }

    @Test
    void update() {
        service.create(ST_1);
        service.update(ST_1_SAME_CODE_ID_1);
        assertThat(service.getById(1L)).isEqualToIgnoringGivenFields(ST_1_SAME_CODE, "id", "totalFaceValue");
    }

    @Test
    void update_null() {
        assertThrows(IllegalArgumentException.class, () -> service.update(null));
    }

    @Test
    void delete() {
        service.create(ST_1);
        assertThat(service.getById(1L).getStatus()).isEqualTo(StockStatus.ACTIVE);
        service.delete(1L);
        assertThat(service.getById(1L).getStatus()).isEqualTo(StockStatus.DELETED);
    }

    @Test
    void delete_notFound() {
        assertThrows(StockNotFoundException.class, () -> service.delete(0L));
    }

    @Test
    void delete_alreadyDeleted() {
        service.create(ST_DELETED);
        if (service.getById(1L).getStatus().equals(StockStatus.ACTIVE)) {
            service.delete(1L);
        }
        assertThrows(StockAlreadyDeletedException.class, () -> service.delete(1L));
    }

    @Test
    void findAll() {
        service.create(ST_1);
        service.create(ST_2);
        service.create(ST_3);
        List<Stock> stockList = service.findAll(null, "", 1, 10)
                .getContent()
                .stream()
                .peek(s -> s.setTotalFaceValue(0L))
                .collect(Collectors.toList());
        assertThat(stockList).isEqualTo(STOCK_LIST);
    }

    @Test
    void findAll_active() {
        service.create(ST_1);
        service.create(ST_2);
        service.create(ST_DELETED);
        if (service.getById(3L).getStatus().equals(StockStatus.ACTIVE)) {
            service.delete(3L);
        }
        service.findAll(StockStatus.ACTIVE, "", 1, 10)
                .getContent()
                .forEach(s -> assertThat(s.getStatus()).isEqualTo(StockStatus.ACTIVE));
    }

    @Test
    void findAll_deleted() {
        service.create(ST_1);
        service.create(ST_2);
        service.create(ST_DELETED);
        if (service.getById(3L).getStatus().equals(StockStatus.ACTIVE)) {
            service.delete(3L);
        }
        service.findAll(StockStatus.DELETED, "", 1, 10)
                .getContent()
                .forEach(s -> assertThat(s.getStatus()).isEqualTo(StockStatus.DELETED));
    }

    @Test
    void findAll_code() {
        service.create(ST_1);
        service.create(ST_2);
        service.create(ST_3);
        List<Stock> stockList = service.findAll(null, "123", 1, 10)
                .getContent()
                .stream()
                .peek(s -> s.setTotalFaceValue(0L))
                .collect(Collectors.toList());
        assertThat(stockList).isEqualTo(STOCK_LIST_1_3);
    }
}