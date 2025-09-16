package com.example.shop.controller;

import com.example.shop.service.ExchangeRateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @GetMapping("/exchange-rates")
    public Map<String, Object> getRates(@RequestParam String baseCurrency) {
        return exchangeRateService.getExchangeRates(baseCurrency);
    }
}
