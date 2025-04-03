package com.example.shop.models;

public class Category {
    private long id;
    private String name;
    private String createdAt;
    private String imageUrl;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(long id, String name, String createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Category(long id, String name, String createdAt, String imageUrl) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}