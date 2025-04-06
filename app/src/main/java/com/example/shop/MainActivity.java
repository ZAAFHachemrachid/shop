package com.example.shop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.example.shop.activities.LoginActivity;
import com.example.shop.managers.AuthManager;
import com.example.shop.utils.ProductInitializer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {
    private static final String TAG = "MainActivity";

    private AuthManager authManager;
    private boolean productsInitialized = false;
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize auth manager
        authManager = AuthManager.getInstance(this);
        
        // Check authentication
        if (!checkAuthentication()) {
            return;
        }

        try {
            setContentView(R.layout.activity_main);

            // Initialize views
            progressBar = findViewById(R.id.progressBar);
            BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
            
            // Set up Navigation Controller using NavHostFragment
            NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
            
            if (navHostFragment != null) {
                NavController navController = navHostFragment.getNavController();
                NavigationUI.setupWithNavController(navView, navController);
            } else {
                Log.e(TAG, "NavHostFragment not found");
            }

            // Initialize products if not done already
            if (!productsInitialized) {
                initializeProducts();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            showError("Error initializing app");
        }
    }

    private boolean checkAuthentication() {
        if (!authManager.isLoggedIn()) {
            // Redirect to login
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check authentication state when resuming
        checkAuthentication();
    }

    private void initializeProducts() {
        progressBar.setVisibility(View.VISIBLE);

        executor.execute(() -> {
            try {
                ProductInitializer.initializeProductsWithImages(MainActivity.this);
                mainHandler.post(() -> {
                    productsInitialized = true;
                    progressBar.setVisibility(View.GONE);
                    showSuccess("Products initialized successfully");
                });
            } catch (Exception e) {
                Log.e(TAG, "Error initializing products: " + e.getMessage(), e);
                mainHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    showError("Error initializing products");
                });
            }
        });
    }

    private void showError(String message) {
        Snackbar.make(findViewById(android.R.id.content), 
            message, Snackbar.LENGTH_LONG).show();
    }

    private void showSuccess(String message) {
        Snackbar.make(findViewById(android.R.id.content), 
            message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}