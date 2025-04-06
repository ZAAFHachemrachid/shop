package com.example.shop.models;

public class Product {
    private long id;
    private String name;
    private String description;
    private double price;
    private String image;
    private long categoryId;
    private String createdAt;
    private double originalPrice;
    private int quantityInStock;
    private float rating;
    private int ratingCount;
    private boolean favorite;
    private transient Category category; // Transient object for UI purposes

    public Product() {
    }

    public Product(String name, String description, double price, String image, long categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.categoryId = categoryId;
    }

    public Product(long id, String name, String description, double price,
                  double originalPrice, String image, long categoryId,
                  int quantityInStock, double rating, int ratingCount,
                  boolean favorite, String createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.image = image;
        this.categoryId = categoryId;
        this.quantityInStock = quantityInStock;
        this.rating = (float) rating;
        this.ratingCount = ratingCount;
        this.favorite = favorite;
        this.createdAt = createdAt;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Get the Category object - may be null if not loaded
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Set the Category object for this product
     */
    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
        }
    }
    
    public Product(long id, String name, String description, double price, String image, long categoryId, String createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.originalPrice = price; // Default to same as price
        this.quantityInStock = 0;
        this.rating = 0;
        this.ratingCount = 0;
        this.favorite = false;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}