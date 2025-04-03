package com.example.shop.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.shop.R;
import com.example.shop.models.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories = new ArrayList<>();
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.bind(categories.get(position));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView categoryImage;
        private final TextView categoryName;
        private final Context context;

        CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryName = itemView.findViewById(R.id.categoryName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onCategoryClick(categories.get(position));
                }
            });
        }

        void bind(Category category) {
            categoryName.setText(category.getName());
            
            // Load category image with Glide
            if (!TextUtils.isEmpty(category.getImageUrl())) {
                Glide.with(context)
                    .load(category.getImageUrl())
                    .apply(new RequestOptions()
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background))
                    .into(categoryImage);
            } else {
                // Use category-specific placeholder based on name
                int placeholderId = getCategoryPlaceholder(category.getName());
                categoryImage.setImageResource(placeholderId);
            }
        }
        
        private int getCategoryPlaceholder(String categoryName) {
            // Return different placeholder icons based on category name
            // This is a simple implementation, you may want to expand it
            if (categoryName == null) {
                return R.drawable.ic_launcher_background;
            }
            
            String name = categoryName.toLowerCase();
            if (name.contains("electronics")) {
                return android.R.drawable.ic_menu_camera;
            } else if (name.contains("clothing")) {
                return android.R.drawable.ic_menu_gallery;
            } else if (name.contains("books")) {
                return android.R.drawable.ic_menu_edit;
            } else if (name.contains("home")) {
                return android.R.drawable.ic_menu_compass;
            } else if (name.contains("beauty")) {
                return android.R.drawable.ic_menu_help;
            } else if (name.contains("sports")) {
                return android.R.drawable.ic_menu_directions;
            } else {
                return R.drawable.ic_launcher_background;
            }
        }
    }
} 