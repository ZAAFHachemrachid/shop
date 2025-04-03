package com.example.shop.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.shop.R;
import com.example.shop.adapters.ProductAdapter;
import com.example.shop.models.Category;
import com.example.shop.models.Product;
import com.example.shop.viewmodels.ProductViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment {

    private RecyclerView productsRecyclerView;
    private ProductAdapter productAdapter;
    private ProductViewModel productViewModel;
    private TextView emptyView;
    private CircularProgressIndicator progressIndicator;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChipGroup categoryChipGroup;
    private TextInputEditText searchInput;
    private RangeSlider priceRangeSlider;
    private MaterialButton sortButton;
    
    // Filter state
    private String currentQuery = "";
    private String selectedCategory = "";
    private float minPrice = 0;
    private float maxPrice = 1000;
    private String sortOrder = "newest"; // newest, price_low, price_high

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup);
        searchInput = view.findViewById(R.id.searchInput);
        priceRangeSlider = view.findViewById(R.id.priceRangeSlider);
        sortButton = view.findViewById(R.id.sortButton);
        
        // Setup RecyclerView
        productAdapter = new ProductAdapter(new ProductAdapter.OnProductClickListener() {
            @Override
            public void onProductClick(Product product) {
                // Handle product click
                // TODO: Navigate to product details
            }
            
            @Override
            public void onAddToCartClick(Product product) {
                // Handle add to cart
            }
        });
        
        productsRecyclerView.setAdapter(productAdapter);
        productsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        
        // Initialize ViewModel
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        
        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::refreshProducts);
        
        // Setup search
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentQuery = s.toString();
                applyFilters();
            }
        });
        
        // Setup price range slider
        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                List<Float> values = slider.getValues();
                minPrice = values.get(0);
                maxPrice = values.get(1);
                applyFilters();
            }
        });
        
        // Setup sort button
        sortButton.setOnClickListener(v -> toggleSortOrder());
        
        // Load categories and populate chips
        loadCategories();
        
        // Load initial products
        loadProducts();
    }
    
    private void loadCategories() {
        // Mock categories for now
        // TODO: Replace with actual data from repository
        List<Category> categories = getMockCategories();
        
        // Add "All" category
        Chip allChip = createCategoryChip("All");
        allChip.setChecked(true);
        categoryChipGroup.addView(allChip);
        
        // Add other categories
        for (Category category : categories) {
            Chip chip = createCategoryChip(category.getName());
            categoryChipGroup.addView(chip);
        }
        
        categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip checkedChip = group.findViewById(checkedId);
            if (checkedChip != null) {
                selectedCategory = "All".equals(checkedChip.getText().toString()) ? "" : checkedChip.getText().toString();
                applyFilters();
            }
        });
    }
    
    private Chip createCategoryChip(String name) {
        Chip chip = new Chip(requireContext());
        chip.setText(name);
        chip.setCheckable(true);
        chip.setClickable(true);
        return chip;
    }
    
    private void loadProducts() {
        showLoading(true);
        
        // TODO: Replace with actual ViewModel call
        // For now, we'll use mock data
        List<Product> products = getMockProducts();
        
        // Apply current filters to mock data
        List<Product> filteredProducts = filterProducts(products);
        
        // Update UI
        productAdapter.setProducts(filteredProducts);
        updateEmptyView(filteredProducts.isEmpty());
        showLoading(false);
        swipeRefreshLayout.setRefreshing(false);
    }
    
    private void refreshProducts() {
        // Simulating a network refresh
        loadProducts();
    }
    
    private void applyFilters() {
        // This would ideally call the viewmodel with the filter parameters
        loadProducts();
    }
    
    private List<Product> filterProducts(List<Product> products) {
        List<Product> result = new ArrayList<>();
        
        for (Product product : products) {
            boolean matchesCategory = selectedCategory.isEmpty() || 
                                      (product.getCategory() != null && 
                                       product.getCategory().getName().equals(selectedCategory));
            
            boolean matchesSearch = currentQuery.isEmpty() || 
                                     product.getName().toLowerCase().contains(currentQuery.toLowerCase());
            
            boolean matchesPrice = product.getPrice() >= minPrice && product.getPrice() <= maxPrice;
            
            if (matchesCategory && matchesSearch && matchesPrice) {
                result.add(product);
            }
        }
        
        // Apply sorting
        if ("price_low".equals(sortOrder)) {
            result.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
        } else if ("price_high".equals(sortOrder)) {
            result.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
        } else {
            // Sort by newest (default)
            // Assuming product has a timestamp or id that can be used for sorting
        }
        
        return result;
    }
    
    private void toggleSortOrder() {
        switch (sortOrder) {
            case "newest":
                sortOrder = "price_low";
                sortButton.setText(R.string.sort_price_low);
                break;
            case "price_low":
                sortOrder = "price_high";
                sortButton.setText(R.string.sort_price_high);
                break;
            case "price_high":
                sortOrder = "newest";
                sortButton.setText(R.string.sort_newest);
                break;
        }
        applyFilters();
    }
    
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressIndicator.setVisibility(View.VISIBLE);
            productsRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        } else {
            progressIndicator.setVisibility(View.GONE);
            productsRecyclerView.setVisibility(View.VISIBLE);
        }
    }
    
    private void updateEmptyView(boolean isEmpty) {
        emptyView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        productsRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
    
    // TODO: Replace with actual data from repository
    private List<Category> getMockCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Electronics", "", "https://images.unsplash.com/photo-1588508065123-287b28e013da?w=400&q=80"));
        categories.add(new Category(2, "Clothing", "", "https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=400&q=80"));
        categories.add(new Category(3, "Books", "", "https://images.unsplash.com/photo-1531988042231-d39a9cc12a9a?w=400&q=80"));
        categories.add(new Category(4, "Home", "", "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=400&q=80"));
        categories.add(new Category(5, "Beauty", "", "https://images.unsplash.com/photo-1526758097130-bab247274f58?w=400&q=80"));
        categories.add(new Category(6, "Sports", "", "https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=400&q=80"));
        return categories;
    }
    
    private List<Product> getMockProducts() {
        List<Product> products = new ArrayList<>();
        List<Category> categories = getMockCategories();
        
        // Create a map for quick category lookup
        java.util.Map<Long, Category> categoryMap = new java.util.HashMap<>();
        for (Category category : categories) {
            categoryMap.put(category.getId(), category);
        }
        
        // Create products with associated categories
        Product p1 = new Product(1, "Smartphone", "Latest model with great camera", 699.99, "", 1, "");
        p1.setCategory(categoryMap.get(1L));
        products.add(p1);
        
        Product p2 = new Product(2, "Laptop", "High performance for work and gaming", 1299.99, "", 1, "");
        p2.setCategory(categoryMap.get(1L));
        products.add(p2);
        
        Product p3 = new Product(3, "T-shirt", "Cotton comfort", 29.99, "", 2, "");
        p3.setCategory(categoryMap.get(2L));
        products.add(p3);
        
        Product p4 = new Product(4, "Jeans", "Classic fit", 59.99, "", 2, "");
        p4.setCategory(categoryMap.get(2L));
        products.add(p4);
        
        Product p5 = new Product(5, "Novel", "Bestseller fiction", 14.99, "", 3, "");
        p5.setCategory(categoryMap.get(3L));
        products.add(p5);
        
        Product p6 = new Product(6, "Lamp", "Modern design", 49.99, "", 4, "");
        p6.setCategory(categoryMap.get(4L));
        products.add(p6);
        
        Product p7 = new Product(7, "Face cream", "Anti-aging formula", 24.99, "", 5, "");
        p7.setCategory(categoryMap.get(5L));
        products.add(p7);
        
        Product p8 = new Product(8, "Basketball", "Professional size", 39.99, "", 6, "");
        p8.setCategory(categoryMap.get(6L));
        products.add(p8);
        
        return products;
    }
}