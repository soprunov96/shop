package com.example.shop.exchangeClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(
        name = "fixerCurrencyApi",  // Feign client name, must be unique if you have multiple Feign clients.
        url = "https://data.fixer.io/api/" // The base URL of the API Fixer.io
)public interface CurrencyExchangeFeignClient {
    @GetMapping("/latest")
    Map<String, Object> getExchangeRates(
            @RequestParam("base") String baseCurrency,
            @RequestParam("access_key") String apiKey // Pass API key for authentication
    );
}
