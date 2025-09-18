package com.example.shop.service;

import com.example.shop.dto.CategoryRequest;
import com.example.shop.models.Category;
import com.example.shop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        if (categoryRequest.getParentId() != null) {
            // Fetch the parent category and link it
            Category parent = categoryRepository.findById(categoryRequest.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found for ID: " + categoryRequest.getParentId()));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    public Category updateCategory(Long categoryId, String name, Long parentId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found for ID: " + categoryId));

        category.setName(name);
        category.setUpdatedAt(LocalDateTime.now());

        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found for ID: " + parentId));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found for ID: " + categoryId));
    }

    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found for ID: " + categoryId));
        categoryRepository.delete(category);
    }

    public String getCategoryPath(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found for ID: " + categoryId));
        return buildCategoryPath(category);
    }

    private String buildCategoryPath(Category category) {
        if (category.getParent() == null) {
            return category.getName();
        }
        return buildCategoryPath(category.getParent()) + " > " + category.getName();
    }
}
