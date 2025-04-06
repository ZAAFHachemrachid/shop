package com.example.shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.shop.activities.LoginActivity;
import com.example.shop.managers.AuthManager;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {
    private static final String TAG = "MainActivity";

    private AuthManager authManager;

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

            // Set up Bottom Navigation
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
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
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
}