package com.example.shop.service;

import com.example.shop.exchangeClient.CurrencyExchangeFeignClient;
import com.example.shop.models.ExchangeRate;
import com.example.shop.repository.ExchangeRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRateService.class);


    @Value("${fixer.api.key}") // Load API key from application.properties
    private String apiKey;

    public void updateExchangeRates() {
        // Fetch exchange rates using Fixer.io API
        Map<String, Object> response = feignClient.getExchangeRates("EUR", apiKey);
        logger.info("Received exchange rate response: {}", response);

        // Extract rates from the response
        Map<String, Object> rates = (Map<String, Object>) response.get("rates");
        if (rates == null) {
            logger.error("Rates field is missing in response");
            throw new IllegalStateException("No 'rates' field found in Fixer.io response");
        }

        // Process the rates
        rates.forEach((currency, rate) -> {
            try {
                BigDecimal rateAsBigDecimal;

                // Handle both Double and Integer
                if (rate instanceof Double) {
                    rateAsBigDecimal = BigDecimal.valueOf((Double) rate);
                } else if (rate instanceof Integer) {
                    // Convert Integer to BigDecimal
                    rateAsBigDecimal = BigDecimal.valueOf((Integer) rate);
                } else {
                    throw new IllegalArgumentException(
                            "Unexpected rate type for currency " + currency + ": " + rate.getClass()
                    );
                }

                // Log the rate type for debugging
                logger.debug("Processing rate for {}: {} (type: {})", currency, rate, rate.getClass());

                // Find or create ExchangeRate entity
                ExchangeRate exchangeRate = exchangeRateRepository
                        .findById(currency)
                        .orElse(new ExchangeRate(currency, rateAsBigDecimal, LocalDateTime.now()));

                // Update the rate and save it
                exchangeRate.setRateToEur(rateAsBigDecimal);
                exchangeRate.setUpdatedAt(LocalDateTime.now());

                exchangeRateRepository.save(exchangeRate);

            } catch (Exception e) {
                logger.error("Error processing rate for {}: {}", currency, e.getMessage(), e);
            }
        });
    }

    public Map<String, Object> getExchangeRates(String baseCurrency) {
        return feignClient.getExchangeRates(baseCurrency, apiKey);
    }
}
