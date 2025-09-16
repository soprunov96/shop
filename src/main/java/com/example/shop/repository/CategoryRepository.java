package com.example.shop.repository;

import com.example.shop.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParent(Category parent);
}


//public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, String> {}
