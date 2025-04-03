package com.example.shop.models;

public class SearchQueryBuilder {
    private String query;
    private Long categoryId;
    private Double minPrice;
    private Double maxPrice;
    private SortOption sortOption;

    public SearchQueryBuilder() {
        this.sortOption = SortOption.DATE_DESC; // Default sort
    }

    public SearchQueryBuilder setQuery(String query) {
        this.query = query;
        return this;
    }

    public SearchQueryBuilder setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
        return this;
    }

    public SearchQueryBuilder setPriceRange(Double minPrice, Double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        return this;
    }

    public SearchQueryBuilder setSortOption(SortOption sortOption) {
        this.sortOption = sortOption;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public SortOption getSortOption() {
        return sortOption;
    }

    public void clear() {
        query = null;
        categoryId = null;
        minPrice = null;
        maxPrice = null;
        sortOption = SortOption.DATE_DESC;
    }

    public boolean hasActiveFilters() {
        return query != null || categoryId != null || minPrice != null || maxPrice != null ||
               sortOption != SortOption.DATE_DESC;
    }
}