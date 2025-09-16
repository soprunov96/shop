package com.example.shop.service;

import com.example.shop.dto.ProductRequest;
import com.example.shop.models.Category;
import com.example.shop.models.ExchangeRate;
import com.example.shop.models.Product;
import com.example.shop.repository.CategoryRepository;
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
    private CategoryRepository categoryRepository;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    public Product createProduct(ProductRequest productRequest) {
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Create the Product
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setCategory(category);
        product.setCurrency(productRequest.getCurrency());
        product.setPrice(productRequest.getPrice());

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
            return product.getPrice();
        } else {
            // Convert to the target currency using exchange rate
            ExchangeRate exchangeRate = exchangeRateRepository.findById(targetCurrency)
                    .orElseThrow(() -> new IllegalArgumentException("Currency not supported: " + targetCurrency));
            return product.getPrice().multiply(exchangeRate.getRateToEur());
        }
    }
}
