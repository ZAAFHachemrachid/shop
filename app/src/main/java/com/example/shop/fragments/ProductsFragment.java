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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.shop.R;
import com.example.shop.adapters.ProductAdapter;
import com.example.shop.db.ShopDatabase;
import com.example.shop.models.Category;
import com.example.shop.models.FilterState;
import com.example.shop.models.Product;
import com.example.shop.models.ProductUiState;
import com.example.shop.repositories.ProductRepository;
import com.example.shop.viewmodels.ProductViewModel;
import com.example.shop.views.ActiveFiltersView;
import com.example.shop.views.CustomPriceRangeView;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
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
    private ShimmerFrameLayout shimmerLayout;
    private TextView emptyView;
    private ActiveFiltersView activeFilters;
    
    private ProductViewModel viewModel;
    private ProductAdapter productAdapter;
    private FilterState currentFilters;

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

        // Initialize ViewModel
        ShopDatabase db = ShopDatabase.getInstance(requireContext());
        ProductRepository repository = new ProductRepository(
            db.productDao(),
            db.cartDao(),
            db.categoryDao()
        );
        
        viewModel = new ViewModelProvider(this, 
            new ProductViewModel.Factory(repository))
            .get(ProductViewModel.class);

        // Initialize adapter
        productAdapter = new ProductAdapter(this);
        productsRecyclerView.setAdapter(productAdapter);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.loadProducts());

        // Setup search input
        setupSearchInput();

        // Setup category chips
        setupCategoryChips();

        // Setup price range slider
        setupPriceRangeSlider();

        // Setup sort button
        setupSortButton();

        // Observe ViewModel state
        observeViewModel();

        // Initial load
        viewModel.loadProducts();
        viewModel.loadCategories();
    }

    private void initViews(View view) {
        searchInput = view.findViewById(R.id.searchInput);
        categoryChipGroup = view.findViewById(R.id.categoryChipGroup);
        priceRangeView = view.findViewById(R.id.priceRangeView);
        sortButton = view.findViewById(R.id.sortButton);
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        emptyView = view.findViewById(R.id.emptyView);
        activeFilters = view.findViewById(R.id.activeFilters);

        activeFilters.setOnFilterRemovedListener(this);
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof ProductUiState.Loading) {
                showLoading();
            } else if (state instanceof ProductUiState.Success) {
                showContent(((ProductUiState.Success) state).getProducts());
            } else if (state instanceof ProductUiState.Error) {
                showError(((ProductUiState.Error) state).getMessage());
            }
        });

        viewModel.getFilterState().observe(getViewLifecycleOwner(), filterState -> {
            currentFilters = filterState;
            updateActiveFilters(filterState);
        });

        viewModel.getCategories().observe(getViewLifecycleOwner(), this::updateCategoryChips);
    }

    private void showLoading() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        emptyView.setVisibility(View.GONE);
        productsRecyclerView.setVisibility(View.GONE);
    }

    private void showContent(List<Product> products) {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        
        if (products.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            productsRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            productsRecyclerView.setVisibility(View.VISIBLE);
            productAdapter.setProducts(products);
        }
    }

    private void showError(String message) {
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show();
    }

    private void setupSearchInput() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateSearchFilter(s.toString().trim());
            }
        });
    }

    private void updateSearchFilter(String query) {
        FilterState newFilters = new FilterState(
            query,
            currentFilters.getCategoryId(),
            currentFilters.getMinPrice(),
            currentFilters.getMaxPrice(),
            currentFilters.getSortOption()
        );
        viewModel.updateFilters(newFilters);
    }

    private void updateCategoryChips(List<Category> categories) {
        categoryChipGroup.removeAllViews();
        
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
                updateCategoryFilter(selectedChip == allCategoriesChip ? null : (Long) selectedChip.getTag());
            }
        });
    }

    private void updateCategoryFilter(Long categoryId) {
        FilterState newFilters = new FilterState(
            currentFilters.getQuery(),
            categoryId,
            currentFilters.getMinPrice(),
            currentFilters.getMaxPrice(),
            currentFilters.getSortOption()
        );
        viewModel.updateFilters(newFilters);
    }

    private void setupPriceRangeSlider() {
        priceRangeView.setOnPriceRangeChangedListener((minPrice, maxPrice) -> {
            FilterState newFilters = new FilterState(
                currentFilters.getQuery(),
                currentFilters.getCategoryId(),
                Double.valueOf(minPrice),
                Double.valueOf(maxPrice),
                currentFilters.getSortOption()
            );
            viewModel.updateFilters(newFilters);
        });
    }

    private void setupSortButton() {
        sortButton.setOnClickListener(v -> showSortOptions());
    }

    private void showSortOptions() {
        // Implementation remains the same
    }

    @Override
    public void onProductClick(Product product) {
        // TODO: Navigate to product details
    }

    @Override
    public void onAddToCartClick(Product product) {
        viewModel.addToCart(product);
    }

    @Override
    public void onFavoriteClick(Product product) {
        viewModel.toggleFavorite(product);
    }

    // Filter removal callbacks implementation
    @Override
    public void onCategoryFilterRemoved() {
        categoryChipGroup.clearCheck();
        updateCategoryFilter(null);
    }

    @Override
    public void onPriceFilterRemoved() {
        priceRangeView.resetPriceRange();
        updatePriceFilter(null, null);
    }

    @Override
    public void onSearchFilterRemoved() {
        searchInput.setText("");
        updateSearchFilter("");
    }

    @Override
    public void onAllFiltersRemoved() {
        viewModel.clearFilters();
        categoryChipGroup.clearCheck();
        priceRangeView.resetPriceRange();
        searchInput.setText("");
    }

    private void updatePriceFilter(Double minPrice, Double maxPrice) {
        FilterState newFilters = new FilterState(
            currentFilters.getQuery(),
            currentFilters.getCategoryId(),
            minPrice,
            maxPrice,
            currentFilters.getSortOption()
        );
        viewModel.updateFilters(newFilters);
    }

    private void updateActiveFilters(FilterState filterState) {
        if (filterState.hasActiveFilters()) {
            activeFilters.setVisibility(View.VISIBLE);
            activeFilters.updateFilters(filterState);
        } else {
            activeFilters.setVisibility(View.GONE);
        }
    }
}