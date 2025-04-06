package com.example.shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.example.shop.R;
import com.example.shop.adapters.BannerAdapter;
import com.example.shop.adapters.CategoryAdapter;
import com.example.shop.adapters.ProductAdapter;
import com.example.shop.db.ShopDatabase;
import com.example.shop.db.dao.CartDao;
import com.example.shop.db.dao.CategoryDao;
import com.example.shop.db.dao.ProductDao;
import com.example.shop.models.Banner;
import com.example.shop.models.CartItem;
import com.example.shop.models.Category;
import com.example.shop.models.Product;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements 
        ProductAdapter.OnProductClickListener,
        CategoryAdapter.OnCategoryClickListener,
        BannerAdapter.OnBannerClickListener {

    private RecyclerView productsRecyclerView;
    private RecyclerView categoriesRecyclerView;
    private ViewPager2 bannerViewPager;
    private TabLayout bannerIndicator;
    private SearchView searchView;
    private ProgressBar progressBar;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView viewAllCategories;
    
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    private BannerAdapter bannerAdapter;
    
    private ProductDao productDao;
    private CartDao cartDao;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initializeViews(view);
        
        // Initialize database
        ShopDatabase db = ShopDatabase.getInstance(requireContext());
        productDao = new ProductDao(db);
        cartDao = new CartDao(db);

        // Setup adapters and listeners
        setupAdapters();
        setupListeners();

        // Initial load
        loadData();
    }

    private void initializeViews(View view) {
        productsRecyclerView = view.findViewById(R.id.productsRecyclerView);
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView);
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        bannerIndicator = view.findViewById(R.id.bannerIndicator);
        searchView = view.findViewById(R.id.searchView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        viewAllCategories = view.findViewById(R.id.viewAllCategories);
    }

    private void setupAdapters() {
        // Products adapter
        productAdapter = new ProductAdapter(this);
        productsRecyclerView.setAdapter(productAdapter);
        
        // Categories adapter
        categoryAdapter = new CategoryAdapter(this);
        categoriesRecyclerView.setAdapter(categoryAdapter);
        
        // Banner adapter
        bannerAdapter = new BannerAdapter(this);
        bannerViewPager.setAdapter(bannerAdapter);
        
        // Setup banner indicator dots
        new TabLayoutMediator(bannerIndicator, bannerViewPager, (tab, position) -> {
            // No text needed for dots
        }).attach();
    }
    
    private void setupListeners() {
        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadData);
        
        // Setup search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Optional: Search as you type
                if (newText.isEmpty()) {
                    loadProducts(); // Reset to all products when search cleared
                }
                return false;
            }
        });
        
        // View all categories click listener
        viewAllCategories.setOnClickListener(v -> navigateToCategories());
    }
    
    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);

        // Load all data
        loadBanners();
        loadCategories();
        loadProducts();
        
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadProducts() {
        // For testing, let's add some dummy products if none exist
        List<Product> products = productDao.getAll(null, null, null, null, null);
        if (products.isEmpty()) {
            addDummyProducts();
            products = productDao.getAll(null, null, null, null, null);
        }

        productAdapter.setProducts(products);

        if (products.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }
    
    private void loadCategories() {
        // For testing, let's add some dummy categories if none exist
        List<Category> categories = getDummyCategories();
        categoryAdapter.setCategories(categories);
    }
    
    private void loadBanners() {
        // For testing, add some dummy banners
        List<Banner> banners = getDummyBanners();
        bannerAdapter.setBanners(banners);
    }
    
    private void searchProducts(String query) {
        List<Product> products = productDao.getAll(query, null, null, null, null);
        productAdapter.setProducts(products);
        
        if (products.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }
    
    private void navigateToCategories() {
        // Navigate to categories fragment
        requireActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.nav_host_fragment, new CategoryListFragment())
            .addToBackStack(null)
            .commit();
    }

    private void addDummyProducts() {
        // First make sure categories exist
        CategoryDao categoryDao = new CategoryDao(ShopDatabase.getInstance(requireContext()));
        if (categoryDao.getAll().isEmpty()) {
            for (Category category : getDummyCategories()) {
                categoryDao.insert(category);
            }
        }

        Product[] dummyProducts = {
            createDummyProduct("Smartphone", "Latest model with great camera", 699.99, 799.99, 1, 50, 4.5f, 128),
            createDummyProduct("Laptop", "High performance for work and gaming", 1299.99, 1499.99, 2, 25, 4.7f, 245),
            createDummyProduct("Headphones", "Wireless with noise cancellation", 199.99, 249.99, 3, 100, 4.3f, 512),
            createDummyProduct("Smartwatch", "Fitness tracker with heart rate monitor", 299.99, 299.99, 4, 75, 4.1f, 89),
            createDummyProduct("Tablet", "10-inch display with stylus support", 499.99, 549.99, 1, 30, 4.6f, 167),
            createDummyProduct("Camera", "4K video with 20MP photos", 799.99, 899.99, 2, 15, 4.8f, 78),
            createDummyProduct("Drone", "4K camera with 30-minute flight time", 999.99, 1199.99, 3, 10, 4.4f, 45),
            createDummyProduct("Gaming Console", "Latest generation with 1TB storage", 599.99, 599.99, 4, 40, 4.9f, 892)
        };

        for (Product product : dummyProducts) {
            productDao.insert(product);
        }
    }

    private Product createDummyProduct(String name, String description, double price,
                                     double originalPrice, long categoryId, int stock,
                                     float rating, int ratingCount) {
        Product product = new Product(name, description, price, "", categoryId);
        product.setOriginalPrice(originalPrice);
        product.setQuantityInStock(stock);
        product.setRating(rating);
        product.setRatingCount(ratingCount);
        product.setFavorite(false);
        return product;
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
    
    private List<Banner> getDummyBanners() {
        List<Banner> banners = new ArrayList<>();
        banners.add(new Banner("Summer Sale", "Up to 50% off on selected items", "", "category", "1"));
        banners.add(new Banner("New Arrivals", "Check out our latest products", "", "category", "2"));
        banners.add(new Banner("Flash Sale", "Limited time offers, act fast!", "", "category", "3"));
        return banners;
    }

    @Override
    public void onProductClick(Product product) {
        // TODO: Navigate to product details
        Toast.makeText(requireContext(), "Selected: " + product.getName(), Toast.LENGTH_SHORT).show();
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
        Toast.makeText(requireContext(), "Added to cart: " + product.getName(), Toast.LENGTH_SHORT).show();
    }
    
    @Override
    public void onCategoryClick(Category category) {
        // Navigate to category products
        Toast.makeText(requireContext(), "Category selected: " + category.getName(), Toast.LENGTH_SHORT).show();
        
        // TODO: Create a ProductListFragment and pass the category ID to filter products
        // For now, we'll just show a toast
    }
    
    @Override
    public void onBannerClick(Banner banner) {
        // TODO: Navigate based on banner target
        Toast.makeText(requireContext(), "Banner clicked: " + banner.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavoriteClick(Product product) {
        product.setFavorite(!product.isFavorite());
        productDao.update(product);
        Toast.makeText(requireContext(),
            product.isFavorite() ? "Added to favorites" : "Removed from favorites",
            Toast.LENGTH_SHORT).show();
    }
}