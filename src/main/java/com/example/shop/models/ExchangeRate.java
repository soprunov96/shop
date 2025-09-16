package com.example.shop.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class ExchangeRate {
    @Id
    private String currencyCode;
    private BigDecimal rateToEur;
    private LocalDateTime updatedAt;

    public ExchangeRate() {
    }

    public ExchangeRate(String currencyCode, BigDecimal rateToEur, LocalDateTime updatedAt) {
        this.currencyCode = currencyCode;
        this.rateToEur = rateToEur;
        this.updatedAt = updatedAt;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getRateToEur() {
        return rateToEur;
    }

    public void setRateToEur(BigDecimal rateToEur) {
        this.rateToEur = rateToEur;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
