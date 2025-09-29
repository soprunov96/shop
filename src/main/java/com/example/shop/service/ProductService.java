package com.example.shop.service;

import com.example.shop.constants.Constants;
import com.example.shop.dto.ProductRequest;
import com.example.shop.exceptions.CategoryNotFoundException;
import com.example.shop.exceptions.CurrencyNotSupportedException;
import com.example.shop.exceptions.ProductNotFoundException;
import com.example.shop.models.Category;
import com.example.shop.models.ExchangeRate;
import com.example.shop.models.Product;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ExchangeRateRepository;
import com.example.shop.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ExchangeRateRepository exchangeRateRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            ExchangeRateRepository exchangeRateRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.exchangeRateRepository = exchangeRateRepository;
    }

    public Product createProduct(ProductRequest productRequest) {
        LOGGER.info("Creating product with name: {}", productRequest.getName());

        // Fetch the category and validate existence
        Category category = categoryRepository.findById(productRequest.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + productRequest.getCategoryId()));

        Product product = buildProduct(productRequest, category);

        if (!Constants.EUR.equalsIgnoreCase(product.getCurrency())) {
            BigDecimal convertedPrice = convertToEur(product.getPrice(), product.getCurrency());
            product.setPrice(convertedPrice);
            product.setCurrency(Constants.EUR);
        }

        Product savedProduct = productRepository.save(product);
        LOGGER.info("Product created successfully with ID: {}", savedProduct.getId());
        return savedProduct;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }

    public BigDecimal getPriceInCurrency(Long productId, String targetCurrency) {
        Product product = getProductById(productId);

        if (Constants.EUR.equalsIgnoreCase(targetCurrency)) {
            return product.getPrice();
        }

        return convertFromEur(product.getPrice(), targetCurrency);
    }

    private BigDecimal convertToEur(BigDecimal amount, String sourceCurrency) {
        LOGGER.info("Converting amount from {} to EUR: {}", sourceCurrency, amount);

        ExchangeRate exchangeRate = exchangeRateRepository.findById(sourceCurrency)
                .orElseThrow(() -> new CurrencyNotSupportedException("Currency not supported: " + sourceCurrency));

        return amount.divide(exchangeRate.getRateToEur(), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal convertFromEur(BigDecimal amount, String targetCurrency) {
        LOGGER.info("Converting amount from EUR to {}: {}", targetCurrency, amount);

        ExchangeRate exchangeRate = exchangeRateRepository.findById(targetCurrency)
                .orElseThrow(() -> new CurrencyNotSupportedException("Currency not supported: " + targetCurrency));

        return amount.multiply(exchangeRate.getRateToEur()).setScale(2, RoundingMode.HALF_UP);
    }

    private Product buildProduct(ProductRequest productRequest, Category category) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setCategory(category);
        product.setCurrency(productRequest.getCurrency());
        product.setPrice(validatePrice(productRequest.getPrice()));
        return product;
    }


    private BigDecimal validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
        return price;
    }
}