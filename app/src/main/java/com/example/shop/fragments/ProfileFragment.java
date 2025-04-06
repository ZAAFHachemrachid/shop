package com.example.shop.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shop.R;
import com.example.shop.activities.LoginActivity;
import com.example.shop.managers.AuthManager;
import com.example.shop.models.User;
import com.google.android.material.button.MaterialButton;

public class ProfileFragment extends Fragment {
    
    private AuthManager authManager;
    private TextView nameTextView;
    private TextView emailTextView;
    private MaterialButton editProfileButton;
    private MaterialButton changePasswordButton;
    private MaterialButton signOutButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        authManager = AuthManager.getInstance(requireContext());
        
        // Initialize views
        initViews(view);
        
        // Set click listeners
        setupClickListeners();
        
        // Load user data
        loadUserData();
        
        return view;
    }

    private void initViews(View view) {
        nameTextView = view.findViewById(R.id.nameTextView);
        emailTextView = view.findViewById(R.id.emailTextView);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);
        signOutButton = view.findViewById(R.id.signOutButton);
    }

    private void setupClickListeners() {
        editProfileButton.setOnClickListener(v -> handleEditProfile());
        changePasswordButton.setOnClickListener(v -> handleChangePassword());
        signOutButton.setOnClickListener(v -> handleSignOut());
    }

    private void loadUserData() {
        User currentUser = authManager.getCurrentUser();
        if (currentUser != null) {
            nameTextView.setText(currentUser.getName());
            emailTextView.setText(currentUser.getEmail());
        }
    }

    private void handleEditProfile() {
        // TODO: Implement edit profile functionality
        Toast.makeText(requireContext(), "Edit profile coming soon", Toast.LENGTH_SHORT).show();
    }

    private void handleChangePassword() {
        // TODO: Implement change password functionality
        Toast.makeText(requireContext(), "Change password coming soon", Toast.LENGTH_SHORT).show();
    }

    private void handleSignOut() {
        authManager.logout();
        // Redirect to login
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData(); // Reload user data when fragment resumes
    }
}