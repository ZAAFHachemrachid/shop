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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class SearchFragment extends Fragment implements ProductAdapter.OnProductClickListener {
    private TextInputEditText searchInput;
    private ChipGroup categoryChipGroup;
    private RangeSlider priceRangeSlider;
    private MaterialButton sortButton;
    private RecyclerView productsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View progressIndicator;
    private TextView emptyView;

    private ProductDao productDao;
    private CategoryDao categoryDao;
    private CartDao cartDao;
    private ProductAdapter productAdapter;
    private SearchQueryBuilder queryBuilder;

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
        priceRangeSlider = view.findViewById(R.id.priceRangeSlider);
        sortButton = view.findViewById(R.id.sortButton);
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        emptyView = view.findViewById(R.id.emptyView);
    }

    private void setupSearchInput() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                queryBuilder.setQuery(s.toString());
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
                loadProducts();
            }
        });
    }

    private void setupPriceRangeSlider() {
        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                List<Float> values = slider.getValues();
                queryBuilder.setPriceRange(
                    values.get(0).doubleValue(),
                    values.get(1).doubleValue()
                );
                loadProducts();
            }
        });
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
}