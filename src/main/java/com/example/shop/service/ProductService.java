package com.example.shop.service;

import com.example.shop.models.ExchangeRate;
import com.example.shop.models.Product;
import com.example.shop.repository.ExchangeRateRepository;
import com.example.shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    public Product createProduct(Product product) {
        // If product is provided in a non-EUR currency, convert to EUR
        if (!"EUR".equalsIgnoreCase(product.getCurrency())) {
            ExchangeRate exchangeRate = exchangeRateRepository.findById(product.getCurrency())
                    .orElseThrow(() -> new IllegalArgumentException("Currency not supported: " + product.getCurrency()));
            BigDecimal convertedPrice = product.getPrice().divide(exchangeRate.getRateToEur(), 2, RoundingMode.HALF_UP);
            product.setPrice(convertedPrice);
            product.setCurrency("EUR"); // Store the converted price in EUR
        }

        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found!"));
    }

    public BigDecimal getPriceInCurrency(Long productId, String targetCurrency) {
        Product product = getProductById(productId);

        if ("EUR".equalsIgnoreCase(targetCurrency)) {
            // Return the price directly if the target currency is EUR
            return product.getPrice();
        } else {
            // Convert to the target currency using exchange rate
            ExchangeRate exchangeRate = exchangeRateRepository.findById(targetCurrency)
                    .orElseThrow(() -> new IllegalArgumentException("Currency not supported: " + targetCurrency));
            return product.getPrice().multiply(exchangeRate.getRateToEur());
        }
    }
}
