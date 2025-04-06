package com.example.shop.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shop.R;
import com.example.shop.adapters.CartAdapter;
import com.example.shop.db.ShopDatabase;
import com.example.shop.models.CartItem;
import com.example.shop.viewmodels.CartViewModel;
import java.text.NumberFormat;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.CartItemListener {
    private CartViewModel viewModel;
    private CartAdapter adapter;
    private RecyclerView recyclerCart;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;
    private TextView textTotal;
    private TextView textItemCount;
    private Button buttonCheckout;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                            @Nullable ViewGroup container,
                            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupRecyclerView();
        setupViewModel();
        observeViewModel();
        setupCheckoutButton();
    }

    private void initializeViews(View view) {
        recyclerCart = view.findViewById(R.id.recyclerCart);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);
        progressBar = view.findViewById(R.id.progressBar);
        textTotal = view.findViewById(R.id.textTotal);
        textItemCount = view.findViewById(R.id.textItemCount);
        buttonCheckout = view.findViewById(R.id.buttonCheckout);
    }

    private void setupRecyclerView() {
        adapter = new CartAdapter(this);
        recyclerCart.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerCart.setAdapter(adapter);
    }

    private void setupViewModel() {
        ShopDatabase database = ShopDatabase.getInstance(requireContext());
        viewModel = new CartViewModel(database);
    }

    private void observeViewModel() {
        viewModel.getCartItems().observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
            updateEmptyState(items.isEmpty());
            updateItemCount(items.size());
        });

        viewModel.getCartTotal().observe(getViewLifecycleOwner(), total -> 
            textTotal.setText(currencyFormat.format(total)));

        viewModel.isLoading().observe(getViewLifecycleOwner(), isLoading -> 
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerCart.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        buttonCheckout.setEnabled(!isEmpty);
    }

    private void updateItemCount(int count) {
        String itemText = count == 1 ? "item" : "items";
        textItemCount.setText(String.format("%d %s", count, itemText));
    }

    private void setupCheckoutButton() {
        buttonCheckout.setOnClickListener(v -> {
            if (!viewModel.isCartEmpty()) {
                // TODO: Implement checkout process
                Toast.makeText(requireContext(), "Proceeding to checkout...", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        viewModel.updateItemQuantity(item, newQuantity);
    }

    @Override
    public void onRemoveItem(CartItem item) {
        viewModel.removeItem(item);
    }
}