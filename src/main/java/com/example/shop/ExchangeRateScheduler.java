package com.example.shop;

import com.example.shop.service.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class ExchangeRateScheduler {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @Scheduled(fixedRate = 86400000) // Update once every 24 hours
    public void updateExchangeRates() {
        exchangeRateService.updateExchangeRates();
    }
}
