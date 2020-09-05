package com.artemfo.stockregister;

import com.artemfo.stockregister.entity.Stock;
import com.artemfo.stockregister.entity.StockStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class TestData {
    public static final Stock ST_1 = new Stock("12345678", 100, 5, LocalDate.parse("2011-01-01"), "one");
    public static final Stock ST_1_SAME_CODE = new Stock("12345678", 1, 1, LocalDate.parse("2011-02-02"), "one");
    public static final Stock ST_1_SAME_CODE_ID_1 = new Stock("12345678", 1, 1, LocalDate.parse("2011-02-02"), "one");
    public static final Stock ST_2 = new Stock("00000008", 10, 75, LocalDate.parse("2012-02-02"), "two");
    public static final Stock ST_2_ID_1 = new Stock("00000008", 10, 75, LocalDate.parse("2012-02-02"), "two");
    public static final Stock ST_3 = new Stock("45678123", 100, 5, LocalDate.parse("2013-03-03"), "three");
    public static final Stock ST_DELETED = new Stock("01010101", 200, 200, LocalDate.parse("2017-07-07"));
    public static final Stock ST_NO_COMMENT = new Stock("00000007", 10, 75, LocalDate.parse("2014-04-04"));
    public static final List<Stock> STOCK_LIST = Arrays.asList(ST_1, ST_2, ST_3);
    public static final List<Stock> STOCK_LIST_1_3 = Arrays.asList(ST_1, ST_3);
    public static final List<Stock> STOCK_LIST_WITH_DELETED = Arrays.asList(ST_1, ST_2, ST_DELETED);
    public static final Pageable PAGEABLE = PageRequest.of(0, 10);
    public static final Page<Stock> PAGE_OF_3 = new PageImpl<>(STOCK_LIST, PAGEABLE, 0);
    public static String JSON_LIST;
    public static String JSON_ST_1;
    public static String JSON_ST_1_SAME_CODE_ID_1;
    public static String JSON_ST_DELETED;

    static {
        ST_DELETED.setStatus(StockStatus.DELETED);
        ST_2_ID_1.setId(1L);
        ST_1_SAME_CODE_ID_1.setId(1L);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            JSON_LIST = objectMapper.writeValueAsString(PAGE_OF_3.getContent());
            JSON_ST_1 = objectMapper.writeValueAsString(ST_1);
            JSON_ST_1_SAME_CODE_ID_1 = objectMapper.writeValueAsString(ST_1_SAME_CODE_ID_1);
            JSON_ST_DELETED = objectMapper.writeValueAsString(ST_DELETED);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
