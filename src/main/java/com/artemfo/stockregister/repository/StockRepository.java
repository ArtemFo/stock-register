package com.artemfo.stockregister.repository;

import com.artemfo.stockregister.entity.Stock;
import com.artemfo.stockregister.entity.StockStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Page<Stock> findAllByStatusAndCodeContains(StockStatus status, String codePart, Pageable pageable);

    Page<Stock> findAllByCodeContains(String codePart, Pageable pageable);
}
