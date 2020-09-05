package com.artemfo.stockregister.controller;

import com.artemfo.stockregister.entity.Stock;
import com.artemfo.stockregister.exception.StockAlreadyDeletedException;
import com.artemfo.stockregister.exception.StockAlreadyExistsException;
import com.artemfo.stockregister.exception.StockNotFoundException;
import com.artemfo.stockregister.service.StockService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static com.artemfo.stockregister.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(StockController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService service;

    @Test
    void findAll() throws Exception {
        when(service.findAll(null, null, 1, 10)).thenReturn(PAGE_OF_3);
        MvcResult result = this.mockMvc.perform(get("/?pageNum={p}&pageSize={s}", 1, 10)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        MockHttpServletResponse response = result.getResponse();
        assertThat(response.getHeader("currentPage")).isEqualTo("1");
        assertThat(response.getHeader("totalPages")).isEqualTo("1");
        assertThat(response.getHeader("totalItems")).isEqualTo("3");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        TypeReference<List<Stock>> typeReference = new TypeReference<List<Stock>>() {
        };
        List<Stock> stockList = objectMapper.readValue(response.getContentAsString(), typeReference);

        assertThat(stockList).isEqualTo(STOCK_LIST);
    }

    @Test
    void createStock() throws Exception {
        when(service.create(ST_1)).thenReturn(ST_1);
        this.mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_ST_1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void createStock_noRequestBody() throws Exception {
        this.mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createStock_alreadyExists() throws Exception {
        ST_1.setId(0);
        doThrow(new StockAlreadyExistsException(ST_1)).when(service).create(ST_1);
        MvcResult result = this.mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_ST_1))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).contains("Already exists stock " + ST_1);
    }

    @Test
    void getById() throws Exception {
        when(service.getById(1L)).thenReturn(ST_1);
        MvcResult result = this.mockMvc.perform(get("/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Stock stock = objectMapper.readValue(result.getResponse().getContentAsString(), Stock.class);

        assertThat(stock).isEqualTo(ST_1);
    }

    @Test
    void getById_notExists() throws Exception {
        when(service.getById(0L)).thenThrow(new StockNotFoundException(0L));
        MvcResult result = this.mockMvc.perform(get("/0"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).contains("Could not find stock 0");
    }

    @Test
    void updateById() throws Exception {
        doNothing().when(service).update(ST_1_SAME_CODE_ID_1);
        this.mockMvc.perform(put("/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON_ST_1_SAME_CODE_ID_1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void updateById_null() throws Exception {
        doNothing().when(service).update(null);
        this.mockMvc.perform(put("/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteById() throws Exception {
        doNothing().when(service).delete(1L);
        this.mockMvc.perform(delete("/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteById_alreadyDeleted() throws Exception {
        doThrow(new StockAlreadyDeletedException(1L)).when(service).delete(1L);
        MvcResult result = this.mockMvc.perform(delete("/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andReturn();
        assertThat(result.getResponse().getContentAsString()).contains("Already deleted stock 1");
    }
}