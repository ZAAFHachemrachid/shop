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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.shop.R;
import com.example.shop.adapters.ProductAdapter;
import com.example.shop.db.ShopDatabase;
import com.example.shop.db.dao.CartDao;
import com.example.shop.db.dao.CategoryDao;
import com.example.shop.db.dao.ProductDao;
import com.example.shop.models.CartItem;
import com.example.shop.models.Category;
import com.example.shop.models.Product;
import com.example.shop.models.SearchQueryBuilder;
import com.example.shop.models.SortOption;
import com.example.shop.views.CustomPriceRangeView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class ProductsFragment extends Fragment implements
        ProductAdapter.OnProductClickListener,
        ActiveFiltersView.OnFilterRemovedListener {
            
    private TextInputEditText searchInput;
    private ChipGroup categoryChipGroup;
    private CustomPriceRangeView priceRangeView;
    private MaterialButton sortButton;
    private RecyclerView productsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View progressIndicator;
    private TextView emptyView;
    private ActiveFiltersView activeFilters;
    private CollapsibleFilterSection categoryFilterSection;
    private CollapsibleFilterSection priceFilterSection;

    private ProductDao productDao;
    private CategoryDao categoryDao;
    private CartDao cartDao;
    private ProductAdapter productAdapter;
    private SearchQueryBuilder queryBuilder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initViews(view);

        // Initialize database
        ShopDatabase db = ShopDatabase.getInstance(requireContext());
        productDao = new ProductDao(db);
        categoryDao = new CategoryDao(db);
        cartDao = new CartDao(db);

        // Initialize adapter and query builder
        productAdapter = new ProductAdapter(this);
        queryBuilder = new SearchQueryBuilder();

        // Setup RecyclerView
        productsRecyclerView.setAdapter(productAdapter);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadProducts);

        // Setup search input
        setupSearchInput();

        // Setup category chips
        setupCategoryChips();

        // Setup price range slider
        setupPriceRangeSlider();

        // Setup sort button
        setupSortButton();

        // Initial load
        loadProducts();
    }

    private void initViews(View view) {
        searchInput = view.findViewById(R.id.searchInput);
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup);
        priceRangeView = view.findViewById(R.id.priceRangeView);
        sortButton = view.findViewById(R.id.sortButton);
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        emptyView = view.findViewById(R.id.emptyView);
        activeFilters = view.findViewById(R.id.activeFilters);
        categoryFilterSection = view.findViewById(R.id.categoryFilterSection);
        priceFilterSection = view.findViewById(R.id.priceFilterSection);

        // Set up filter sections
        categoryFilterSection.setTitle(R.string.filter_by_category);
        priceFilterSection.setTitle(R.string.price_range);

        // Set up active filters
        activeFilters.setOnFilterRemovedListener(this);
    }

    private void setupSearchInput() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                queryBuilder.setQuery(query);
                
                if (!query.isEmpty()) {
                    activeFilters.showSearchFilter(query);
                    activeFilters.setVisibility(View.VISIBLE);
                }
                
                loadProducts();
            }
        });
    }

    private void setupCategoryChips() {
        List<Category> categories = categoryDao.getAll();
        
        // Add "All Categories" chip
        Chip allCategoriesChip = new Chip(requireContext());
        allCategoriesChip.setText(R.string.all_categories);
        allCategoriesChip.setCheckable(true);
        allCategoriesChip.setChecked(true);
        categoryChipGroup.addView(allCategoriesChip);

        // Add category chips
        for (Category category : categories) {
            Chip chip = new Chip(requireContext());
            chip.setText(category.getName());
            chip.setCheckable(true);
            chip.setTag(category.getId());
            categoryChipGroup.addView(chip);
        }

        categoryChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Chip selectedChip = group.findViewById(checkedId);
            if (selectedChip != null) {
                Long categoryId = selectedChip == allCategoriesChip ? null : (Long) selectedChip.getTag();
                queryBuilder.setCategoryId(categoryId);
                
                if (categoryId != null) {
                    Category category = categoryDao.findById(categoryId);
                    activeFilters.showCategoryFilter(category);
                    activeFilters.setVisibility(View.VISIBLE);
                }
                
                loadProducts();
            }
        });
    }

    private void setupPriceRangeSlider() {
        // Set initial value range based on product prices
        priceRangeView.setValueRange(0f, 10000f);
        // Set initial price range
        priceRangeView.setPriceRange(0f, 10000f);
        
        priceRangeView.setOnPriceRangeChangedListener((minPrice, maxPrice) -> {
            queryBuilder.setPriceRange(Double.valueOf(minPrice), Double.valueOf(maxPrice));
            activeFilters.showPriceFilter(minPrice, maxPrice);
            activeFilters.setVisibility(View.VISIBLE);
            loadProducts();
        });
    }

    @Override
    public void onCategoryFilterRemoved() {
        categoryChipGroup.clearCheck();
        queryBuilder.setCategoryId(null);
        loadProducts();
        if (!queryBuilder.hasActiveFilters()) {
            activeFilters.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPriceFilterRemoved() {
        priceRangeView.setPriceRange(0f, 10000f);
        queryBuilder.setPriceRange(null, null);
        loadProducts();
        if (!queryBuilder.hasActiveFilters()) {
            activeFilters.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSearchFilterRemoved() {
        searchInput.setText("");
        queryBuilder.setQuery(null);
        loadProducts();
        if (!queryBuilder.hasActiveFilters()) {
            activeFilters.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAllFiltersRemoved() {
        searchInput.setText("");
        categoryChipGroup.clearCheck();
        priceRangeView.setPriceRange(0f, 10000f);
        queryBuilder.clear();
        loadProducts();
        activeFilters.setVisibility(View.GONE);
    }

    private void setupSortButton() {
        sortButton.setOnClickListener(v -> showSortOptions());
    }

    private void showSortOptions() {
        String[] options = new String[SortOption.values().length];
        int i = 0;
        for (SortOption option : SortOption.values()) {
            options[i++] = option.getDisplayName();
        }

        new android.app.AlertDialog.Builder(requireContext())
            .setTitle(R.string.sort_by)
            .setItems(options, (dialog, which) -> {
                SortOption selectedOption = SortOption.values()[which];
                queryBuilder.setSortOption(selectedOption);
                sortButton.setText(selectedOption.getDisplayName());
                loadProducts();
            })
            .show();
    }

    private void loadProducts() {
        progressIndicator.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        List<Product> products = productDao.getAll(
            queryBuilder.getQuery(),
            queryBuilder.getCategoryId(),
            queryBuilder.getMinPrice(),
            queryBuilder.getMaxPrice(),
            queryBuilder.getSortOption()
        );

        productAdapter.setProducts(products);
        progressIndicator.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        if (products.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProductClick(Product product) {
        // TODO: Navigate to product details
    }

    @Override
    public void onAddToCartClick(Product product) {
        CartItem existingItem = cartDao.findByProductId(product.getId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
            cartDao.update(existingItem);
        } else {
            CartItem newItem = new CartItem(product.getId(), 1);
            cartDao.insert(newItem);
        }
    }

    @Override
    public void onFavoriteClick(Product product) {
        product.setFavorite(!product.isFavorite());
        productDao.update(product);
    }
}