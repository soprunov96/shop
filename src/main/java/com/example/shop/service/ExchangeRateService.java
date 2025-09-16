package com.example.shop.service;

import com.example.shop.exchangeClient.CurrencyExchangeFeignClient;
import com.example.shop.models.ExchangeRate;
import com.example.shop.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ExchangeRateService {

    @Autowired
    private CurrencyExchangeFeignClient feignClient;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Value("${fixer.api.key}") // Load API key from application.properties
    private String apiKey;

    public void updateExchangeRates() {
        // Fetch exchange rates using Fixer.io API
        Map<String, Object> response = feignClient.getExchangeRates("EUR", apiKey);

        // Extract rates from the response
        Map<String, BigDecimal> rates = (Map<String, BigDecimal>) response.get("rates");

        // Update or insert exchange rates in the database
        rates.forEach((currency, rate) -> {
            ExchangeRate exchangeRate = exchangeRateRepository
                    .findById(currency)
                    .orElse(new ExchangeRate(currency, rate, LocalDateTime.now()));

            exchangeRate.setRateToEur(rate);
            exchangeRate.setUpdatedAt(LocalDateTime.now());
            exchangeRateRepository.save(exchangeRate);
        });
    }

    public Map<String, Object> getExchangeRates(String baseCurrency) {
        return feignClient.getExchangeRates(baseCurrency, apiKey);
    }
}
