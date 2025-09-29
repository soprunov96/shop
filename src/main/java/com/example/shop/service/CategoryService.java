package com.example.shop.service;

import com.example.shop.dto.CategoryRequest;
import com.example.shop.exceptions.CategoryNotFoundException;
import com.example.shop.models.Category;
import com.example.shop.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new category from the given request.
     * @param categoryRequest DTO containing category details
     * @return the newly created category
     */
    public Category createCategory(CategoryRequest categoryRequest) {
        Category category = initializeCategory(categoryRequest.getName());

        if (categoryRequest.getParentId() != null) {
            category.setParent(getCategoryByIdOrThrow(categoryRequest.getParentId(), "Parent category not found for ID: "));
        }

        return categoryRepository.save(category);
    }

    /**
     * Updates an existing category by ID.
     * @param categoryId the ID of the category to update
     * @param name the new name for the category
     * @param parentId the ID of the optional parent category
     * @return the updated category
     */
    public Category updateCategory(Long categoryId, String name, Long parentId) {
        Category category = getCategoryByIdOrThrow(categoryId, "Category not found for ID: ");

        category.setName(name);
        category.setUpdatedAt(LocalDateTime.now());

        // Set and link the parent category (if present).
        if (parentId != null) {
            category.setParent(getCategoryByIdOrThrow(parentId, "Parent category not found for ID: "));
        }

        return categoryRepository.save(category);
    }

    /**
     * Retrieves all categories in the repository.
     * @return a list of all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Retrieves a category by ID.
     * @param categoryId the ID of the category
     * @return the found category
     */
    public Category getCategoryById(Long categoryId) {
        return getCategoryByIdOrThrow(categoryId, "Category not found for ID: ");
    }

    /**
     * Deletes a category by ID.
     * @param categoryId the ID of the category to delete
     */
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryByIdOrThrow(categoryId, "Category not found for ID: ");
        categoryRepository.delete(category);
    }

    /**
     * Creates and initializes a category with metadata.
     * @param name the name of the new category
     * @return the initialized category
     */
    private Category initializeCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return category;
    }

    /**
     * Fetches a category by ID and throws an exception if not found.
     * @param categoryId the ID of the category to fetch
     * @param errorMessage the error message to throw if the category isn't found
     * @return the fetched category
     */
    private Category getCategoryByIdOrThrow(Long categoryId, String errorMessage) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(errorMessage + categoryId));
    }
}