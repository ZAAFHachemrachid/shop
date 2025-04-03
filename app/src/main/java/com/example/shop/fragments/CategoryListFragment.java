package com.example.shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.shop.R;
import com.example.shop.adapters.CategoryGridAdapter;
import com.example.shop.db.ShopDatabase;
import com.example.shop.db.dao.CategoryDao;
import com.example.shop.models.Category;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;

public class CategoryListFragment extends Fragment implements CategoryGridAdapter.OnCategoryClickListener {

    private RecyclerView categoriesRecyclerView;
    private CategoryGridAdapter categoryAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyView;
    private CircularProgressIndicator progressIndicator;
    private CategoryDao categoryDao;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        categoryDao = new CategoryDao(ShopDatabase.getInstance(requireContext()));

        // Initialize views
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        emptyView = view.findViewById(R.id.emptyView);
        progressIndicator = view.findViewById(R.id.progressIndicator);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        // Setup RecyclerView
        categoryAdapter = new CategoryGridAdapter(this);
        categoriesRecyclerView.setAdapter(categoryAdapter);
        categoriesRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // Setup toolbar
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());

        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(this::loadCategories);

        // Load data
        loadCategories();
    }

    private void loadCategories() {
        progressIndicator.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        // For testing, let's add some dummy categories if none exist
        List<Category> categories = getDummyCategories();
        categoryAdapter.setCategories(categories);

        progressIndicator.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

        if (categories.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private List<Category> getDummyCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Electronics", "", "https://images.unsplash.com/photo-1588508065123-287b28e013da?w=400&q=80"));
        categories.add(new Category(2, "Clothing", "", "https://images.unsplash.com/photo-1523381210434-271e8be1f52b?w=400&q=80"));
        categories.add(new Category(3, "Books", "", "https://images.unsplash.com/photo-1531988042231-d39a9cc12a9a?w=400&q=80"));
        categories.add(new Category(4, "Home", "", "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=400&q=80"));
        categories.add(new Category(5, "Beauty", "", "https://images.unsplash.com/photo-1526758097130-bab247274f58?w=400&q=80"));
        categories.add(new Category(6, "Sports", "", "https://images.unsplash.com/photo-1517466787929-bc90951d0974?w=400&q=80"));
        categories.add(new Category(7, "Food", "", "https://images.unsplash.com/photo-1504754524776-8f4f37790ca0?w=400&q=80"));
        categories.add(new Category(8, "Toys", "", "https://images.unsplash.com/photo-1566576912321-d58ddd7a6088?w=400&q=80"));
        return categories;
    }

    @Override
    public void onCategoryClick(Category category) {
        // TODO: Navigate to products in this category
        Toast.makeText(requireContext(), "Selected category: " + category.getName(), Toast.LENGTH_SHORT).show();
    }
} 