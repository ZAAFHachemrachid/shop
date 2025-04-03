package com.example.shop.models;

public class Banner {
    private long id;
    private String title;
    private String description;
    private String image;
    private String targetType; // "product", "category", "url"
    private String targetId;

    public Banner() {
    }

    public Banner(String title, String description, String image, String targetType, String targetId) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    public Banner(long id, String title, String description, String image, String targetType, String targetId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image = image;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
} 