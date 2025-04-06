package com.example.shop.adapters;

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
import com.example.shop.models.Banner;
import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private List<Banner> banners = new ArrayList<>();
    private OnBannerClickListener listener;

    public interface OnBannerClickListener {
        void onBannerClick(Banner banner);
    }

    public BannerAdapter(OnBannerClickListener listener) {
        this.listener = listener;
    }

    public void setBanners(List<Banner> banners) {
        this.banners = banners;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bind(banners.get(position));
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView bannerImage;
        private final TextView bannerTitle;
        private final TextView bannerDescription;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
            bannerTitle = itemView.findViewById(R.id.bannerTitle);
            bannerDescription = itemView.findViewById(R.id.bannerDescription);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBannerClick(banners.get(position));
                }
            });
        }

        void bind(Banner banner) {
            bannerTitle.setText(banner.getTitle());
            bannerDescription.setText(banner.getDescription());
            
            // Load banner image using Glide
            if (!TextUtils.isEmpty(banner.getImage())) {
                try {
                    // For drawable resources
                    if (banner.getImage().startsWith("drawable-nodpi/") ||
                        banner.getImage().startsWith("@drawable/")) {
                        
                        // Extract resource name from path
                        String resourceName = banner.getImage();
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
                                .into(bannerImage);
                        } else {
                            // Resource not found, use placeholder
                            bannerImage.setImageResource(R.drawable.ic_launcher_background);
                        }
                    } else {
                        // For file paths or URLs
                        Glide.with(itemView.getContext())
                            .load(banner.getImage())
                            .apply(new RequestOptions()
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_launcher_background)
                                .centerCrop())
                            .into(bannerImage);
                    }
                } catch (Exception e) {
                    // If any error occurs, use placeholder
                    bannerImage.setImageResource(R.drawable.ic_launcher_background);
                }
            } else {
                // Use placeholder if no image available
                bannerImage.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
} 