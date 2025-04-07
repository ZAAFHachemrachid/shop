package com.example.shop.models;

import androidx.annotation.Nullable;
import java.util.Objects;

public class FilterState {
    @Nullable private String query;
    @Nullable private Long categoryId;
    @Nullable private Double minPrice;
    @Nullable private Double maxPrice;
    @Nullable private SortOption sortOption;

    public FilterState() {
        // Default constructor with no filters
    }

    public FilterState(@Nullable String query, @Nullable Long categoryId,
                      @Nullable Double minPrice, @Nullable Double maxPrice,
                      @Nullable SortOption sortOption) {
        this.query = query;
        this.categoryId = categoryId;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.sortOption = sortOption;
    }

    @Nullable
    public String getQuery() {
        return query;
    }

    public void setQuery(@Nullable String query) {
        this.query = query;
    }

    @Nullable
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@Nullable Long categoryId) {
        this.categoryId = categoryId;
    }

    @Nullable
    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(@Nullable Double minPrice) {
        this.minPrice = minPrice;
    }

    @Nullable
    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(@Nullable Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    @Nullable
    public SortOption getSortOption() {
        return sortOption;
    }

    public void setSortOption(@Nullable SortOption sortOption) {
        this.sortOption = sortOption;
    }

    public boolean hasActiveFilters() {
        return query != null || categoryId != null || 
               minPrice != null || maxPrice != null || 
               sortOption != null;
    }

    public void clear() {
        query = null;
        categoryId = null;
        minPrice = null;
        maxPrice = null;
        sortOption = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterState that = (FilterState) o;
        return Objects.equals(query, that.query) &&
                Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(minPrice, that.minPrice) &&
                Objects.equals(maxPrice, that.maxPrice) &&
                sortOption == that.sortOption;
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, categoryId, minPrice, maxPrice, sortOption);
    }

    @Override
    public String toString() {
        return "FilterState{" +
                "query='" + query + '\'' +
                ", categoryId=" + categoryId +
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", sortOption=" + sortOption +
                '}';
    }
}