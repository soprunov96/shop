package com.example.shop.dto;

public class CategoryRequest {

    private String name;
    private Long parentId; // The ID of the parent category (can be null for root categories)

    public CategoryRequest() {
    }

    public CategoryRequest(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
