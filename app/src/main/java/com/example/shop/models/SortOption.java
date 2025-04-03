package com.example.shop.models;

/**
 * Enum for defining product sorting options
 */
public enum SortOption {
    // For backward compatibility with existing code
    DATE_DESC("Newest First", "created_at DESC"),
    DATE_ASC("Oldest First", "created_at ASC"),
    PRICE_ASC("Price: Low to High", "price ASC"),
    PRICE_DESC("Price: High to Low", "price DESC"),
    NAME_ASC("Name (A-Z)", "name ASC"),
    NAME_DESC("Name (Z-A)", "name DESC");

    private final String displayName;
    private final String sqlOrder;

    SortOption(String displayName, String sqlOrder) {
        this.displayName = displayName;
        this.sqlOrder = sqlOrder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSqlOrder() {
        return sqlOrder;
    }

    @Override
    public String toString() {
        return displayName;
    }
}