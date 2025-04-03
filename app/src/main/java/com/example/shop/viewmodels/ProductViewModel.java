package com.example.shop.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shop.models.Product;
import com.example.shop.models.SortOption;

import java.util.List;

/**
 * ViewModel for handling product data and filtering operations
 */
public class ProductViewModel extends ViewModel {
    
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    // Filter parameters
    private String query;
    private Long categoryId;
    private Double minPrice;
    private Double maxPrice;
    private SortOption sortOption = SortOption.DATE_DESC;
    
    /**
     * Load products based on current filter settings
     */
    public void loadProducts() {
        isLoading.setValue(true);
        // TODO: Implement actual data loading from repository
        isLoading.setValue(false);
    }
    
    /**
     * Set search query filter
     */
    public void setQuery(String query) {
        this.query = query;
    }
    
    /**
     * Set category filter
     */
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    /**
     * Set price range filter
     */
    public void setPriceRange(Double minPrice, Double maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
    
    /**
     * Set sort option
     */
    public void setSortOption(SortOption sortOption) {
        this.sortOption = sortOption;
    }
    
    /**
     * Clear all filters
     */
    public void clearFilters() {
        query = null;
        categoryId = null;
        minPrice = null;
        maxPrice = null;
        sortOption = SortOption.DATE_DESC;
    }
    
    /**
     * Get the product list LiveData
     */
    public LiveData<List<Product>> getProducts() {
        return products;
    }
    
    /**
     * Get the loading state LiveData
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
} 