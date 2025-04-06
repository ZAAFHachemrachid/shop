package com.example.shop.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shop.R;
import com.example.shop.models.Product;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products = new ArrayList<>();
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
        void onFavoriteClick(Product product);
    }

    public ProductAdapter(OnProductClickListener listener) {
        this.listener = listener;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView productImage;
        private final MaterialButton favoriteButton;
        private final TextView productName;
        private final TextView productDescription;
        private final TextView productPrice;
        private final TextView originalPrice;
        private final Chip discountChip;
        private final RatingBar ratingBar;
        private final TextView ratingCount;
        private final MaterialButton addToCartButton;
        private final TextView outOfStockLabel;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productPrice = itemView.findViewById(R.id.productPrice);
            originalPrice = itemView.findViewById(R.id.originalPrice);
            discountChip = itemView.findViewById(R.id.discountChip);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingCount = itemView.findViewById(R.id.ratingCount);
            addToCartButton = itemView.findViewById(R.id.addToCartButton);
            outOfStockLabel = itemView.findViewById(R.id.outOfStockLabel);

            setupClickListeners();
        }

        private void setupClickListeners() {
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onProductClick(products.get(position));
                }
            });

            addToCartButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onAddToCartClick(products.get(position));
                }
            });

            favoriteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onFavoriteClick(products.get(position));
                }
            });
        }

        void bind(Product product) {
            productName.setText(product.getName());
            productDescription.setText(product.getDescription());
            productPrice.setText(String.format(
                itemView.getContext().getString(R.string.price_format),
                product.getPrice()
            ));

            // Handle discount if available
            if (product.getOriginalPrice() > product.getPrice()) {
                originalPrice.setVisibility(View.VISIBLE);
                originalPrice.setText(String.format(
                    itemView.getContext().getString(R.string.price_format),
                    product.getOriginalPrice()
                ));
                originalPrice.setPaintFlags(originalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);

                int discountPercentage = calculateDiscount(
                    product.getOriginalPrice(),
                    product.getPrice()
                );
                discountChip.setVisibility(View.VISIBLE);
                discountChip.setText(String.format(
                    itemView.getContext().getString(R.string.discount_format),
                    discountPercentage
                ));
            } else {
                originalPrice.setVisibility(View.GONE);
                discountChip.setVisibility(View.GONE);
            }

            // Handle rating
            if (product.getRating() > 0) {
                ratingBar.setVisibility(View.VISIBLE);
                ratingCount.setVisibility(View.VISIBLE);
                ratingBar.setRating((float) product.getRating());
                ratingCount.setText(String.format(
                    itemView.getContext().getString(R.string.rating_count),
                    product.getRatingCount()
                ));
            } else {
                ratingBar.setVisibility(View.GONE);
                ratingCount.setVisibility(View.GONE);
            }

            // Handle stock status
            boolean inStock = product.getQuantityInStock() > 0;
            addToCartButton.setVisibility(inStock ? View.VISIBLE : View.GONE);
            outOfStockLabel.setVisibility(inStock ? View.GONE : View.VISIBLE);

            // Handle favorite status
            favoriteButton.setIconResource(
                product.isFavorite() ?
                R.drawable.ic_favorite :
                R.drawable.ic_favorite_border
            );
            
            // Load product image using Glide
            if (!TextUtils.isEmpty(product.getImage())) {
                try {
                    // For drawable resources
                    if (product.getImage().startsWith("drawable-nodpi/") || 
                        product.getImage().startsWith("@drawable/")) {
                        
                        // Extract resource name from path
                        String resourceName = product.getImage();
                        if (resourceName.contains("/")) {
                            resourceName = resourceName.substring(resourceName.lastIndexOf("/") + 1);
                        }
                        if (resourceName.contains(".")) {
                            resourceName = resourceName.substring(0, resourceName.lastIndexOf("."));
                        }
                        
                        // Get resource ID dynamically
                        int resourceId = itemView.getContext().getResources().getIdentifier(
                            resourceName, "drawable", itemView.getContext().getPackageName());
                        
                        if (resourceId != 0) {
                            Glide.with(itemView.getContext())
                                .load(resourceId)
                                .apply(new RequestOptions()
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.ic_launcher_background)
                                    .centerCrop())
                                .into(productImage);
                        } else {
                            // Resource not found, use placeholder
                            productImage.setImageResource(R.drawable.ic_launcher_background);
                        }
                    } else {
                        // For file paths or URLs
                        Glide.with(itemView.getContext())
                            .load(product.getImage())
                            .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_launcher_background)
                                .centerCrop())
                            .into(productImage);
                    }
                } catch (Exception e) {
                    // If any error occurs, use placeholder
                    productImage.setImageResource(R.drawable.ic_launcher_background);
                }
            } else {
                // Use placeholder if no image available
                productImage.setImageResource(R.drawable.ic_launcher_background);
            }
        }

        private int calculateDiscount(double originalPrice, double currentPrice) {
            return (int) ((1 - (currentPrice / originalPrice)) * 100);
        }
    }
}