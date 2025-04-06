package com.example.shop.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.shop.R;
import com.example.shop.models.CartItem;
import java.util.ArrayList;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItems = new ArrayList<>();
    private final CartItemListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

    public interface CartItemListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onRemoveItem(CartItem item);
    }

    public CartAdapter(CartItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void submitList(List<CartItem> items) {
        this.cartItems = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageProduct;
        private final TextView textProductName;
        private final TextView textPrice;
        private final TextView textQuantity;
        private final TextView textSubtotal;
        private final ImageButton buttonDecrease;
        private final ImageButton buttonIncrease;
        private final ImageButton buttonRemove;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProduct = itemView.findViewById(R.id.imageProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textPrice = itemView.findViewById(R.id.textPrice);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textSubtotal = itemView.findViewById(R.id.textSubtotal);
            buttonDecrease = itemView.findViewById(R.id.buttonDecrease);
            buttonIncrease = itemView.findViewById(R.id.buttonIncrease);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }

        void bind(CartItem item) {
            if (item.getProduct() != null) {
                textProductName.setText(item.getProduct().getName());
                textPrice.setText(String.format("Price: %s", 
                    currencyFormat.format(item.getProduct().getPrice())));
                // TODO: Load product image using Glide or similar library
                // Glide.with(imageProduct).load(item.getProduct().getImageUrl()).into(imageProduct);
            }

            textQuantity.setText(String.valueOf(item.getQuantity()));
            textSubtotal.setText(currencyFormat.format(item.getSubtotal()));

            buttonDecrease.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() - 1;
                if (newQuantity >= 0) {
                    listener.onQuantityChanged(item, newQuantity);
                }
                if (newQuantity == 0) {
                    listener.onRemoveItem(item);
                }
            });

            buttonIncrease.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() + 1;
                if (newQuantity <= 99) { // Set a reasonable maximum quantity
                    listener.onQuantityChanged(item, newQuantity);
                }
            });

            buttonRemove.setOnClickListener(v -> listener.onRemoveItem(item));
        }
    }
}