package com.example.shop.models;

import androidx.annotation.NonNull;
import java.util.List;

public abstract class ProductUiState {
    
    private ProductUiState() {
        // Private constructor to prevent direct instantiation
    }

    public static final class Loading extends ProductUiState {
        public Loading() {}

        @NonNull
        @Override
        public String toString() {
            return "ProductUiState.Loading";
        }
    }

    public static final class Success extends ProductUiState {
        private final List<Product> products;

        public Success(List<Product> products) {
            this.products = products;
        }

        public List<Product> getProducts() {
            return products;
        }

        @NonNull
        @Override
        public String toString() {
            return "ProductUiState.Success{" +
                    "products=" + products +
                    '}';
        }
    }

    public static final class Error extends ProductUiState {
        private final String message;
        private final Throwable error;

        public Error(String message, Throwable error) {
            this.message = message;
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public Throwable getError() {
            return error;
        }

        @NonNull
        @Override
        public String toString() {
            return "ProductUiState.Error{" +
                    "message='" + message + '\'' +
                    ", error=" + error +
                    '}';
        }
    }
}