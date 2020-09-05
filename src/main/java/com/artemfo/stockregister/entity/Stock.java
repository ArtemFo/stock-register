package com.artemfo.stockregister.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Entity
@Data
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @Pattern(regexp = "[0-9]{8}")
    @Column(unique = true)
    private String code;

    @PositiveOrZero
    private int amount;

    @PositiveOrZero
    private int faceValue;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Formula("amount * face_value")
    private long totalFaceValue;

    @Enumerated(EnumType.STRING)
    private StockStatus status;

    private String comment;

    public Stock() {
    }

    public Stock(String code, int amount, int faceValue, LocalDate date, String comment) {
        this.code = code;
        this.amount = amount;
        this.faceValue = faceValue;
        this.date = date;
        this.comment = comment;
        this.status = StockStatus.ACTIVE;
    }

    public Stock(String code, int amount, int faceValue, LocalDate date) {
        this(code, amount, faceValue, date, "");
    }
}
