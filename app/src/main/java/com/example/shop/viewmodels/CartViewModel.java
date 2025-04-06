package com.example.shop.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.shop.db.ShopDatabase;
import com.example.shop.db.dao.CartDao;
import com.example.shop.models.CartItem;
import java.util.List;

public class CartViewModel extends ViewModel {
    private final CartDao cartDao;
    private final MutableLiveData<List<CartItem>> cartItems = new MutableLiveData<>();
    private final MutableLiveData<Double> cartTotal = new MutableLiveData<>(0.0);
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public CartViewModel(ShopDatabase database) {
        this.cartDao = new CartDao(database);
        loadCartItems();
    }

    public LiveData<List<CartItem>> getCartItems() {
        return cartItems;
    }

    public LiveData<Double> getCartTotal() {
        return cartTotal;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadCartItems() {
        isLoading.setValue(true);
        try {
            List<CartItem> items = cartDao.getAll();
            cartItems.setValue(items);
            updateCartTotal();
        } catch (Exception e) {
            error.setValue("Failed to load cart items: " + e.getMessage());
        } finally {
            isLoading.setValue(false);
        }
    }

    public void updateItemQuantity(CartItem item, int newQuantity) {
        try {
            item.setQuantity(newQuantity);
            cartDao.update(item);
            loadCartItems(); // Reload to refresh the list and totals
        } catch (Exception e) {
            error.setValue("Failed to update item quantity: " + e.getMessage());
        }
    }

    public void removeItem(CartItem item) {
        try {
            cartDao.delete(item.getId());
            loadCartItems(); // Reload to refresh the list and totals
        } catch (Exception e) {
            error.setValue("Failed to remove item: " + e.getMessage());
        }
    }

    public void clearCart() {
        try {
            cartDao.clearCart();
            loadCartItems(); // Reload to refresh the list and totals
        } catch (Exception e) {
            error.setValue("Failed to clear cart: " + e.getMessage());
        }
    }

    private void updateCartTotal() {
        List<CartItem> items = cartItems.getValue();
        if (items != null) {
            double total = 0;
            for (CartItem item : items) {
                total += item.getSubtotal();
            }
            cartTotal.setValue(total);
        }
    }

    // Helper method to check if cart is empty
    public boolean isCartEmpty() {
        List<CartItem> items = cartItems.getValue();
        return items == null || items.isEmpty();
    }
}