package com.example.shop.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.shop.R;
import com.example.shop.models.Category;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class ActiveFiltersView extends FrameLayout {
    private ChipGroup chipGroup;
    private final NumberFormat currencyFormat;
    private OnFilterRemovedListener listener;

    public interface OnFilterRemovedListener {
        void onCategoryFilterRemoved();
        void onPriceFilterRemoved();
        void onSearchFilterRemoved();
        void onAllFiltersRemoved();
    }

    public ActiveFiltersView(@NonNull Context context) {
        this(context, null);
    }

    public ActiveFiltersView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActiveFiltersView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fr", "DZ"));
        currencyFormat.setCurrency(Currency.getInstance("DZD"));
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_active_filters, this, true);
        chipGroup = findViewById(R.id.activeFiltersChipGroup);
    }

    public void showCategoryFilter(Category category) {
        removeCategoryFilter(); // Remove existing category filter if any
        
        Chip chip = new Chip(getContext());
        chip.setText(category.getName());
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            if (listener != null) {
                listener.onCategoryFilterRemoved();
            }
            updateClearAllVisibility();
        });
        
        chip.setTag("category");
        chipGroup.addView(chip);
        updateClearAllVisibility();
    }

    public void showPriceFilter(double minPrice, double maxPrice) {
        removePriceFilter(); // Remove existing price filter if any
        
        Chip chip = new Chip(getContext());
        String priceRange = String.format(
            getContext().getString(R.string.price_range_label),
            currencyFormat.format(minPrice),
            currencyFormat.format(maxPrice)
        );
        chip.setText(priceRange);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            if (listener != null) {
                listener.onPriceFilterRemoved();
            }
            updateClearAllVisibility();
        });
        
        chip.setTag("price");
        chipGroup.addView(chip);
        updateClearAllVisibility();
    }

    public void showSearchFilter(String query) {
        removeSearchFilter(); // Remove existing search filter if any
        
        Chip chip = new Chip(getContext());
        chip.setText(query);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            if (listener != null) {
                listener.onSearchFilterRemoved();
            }
            updateClearAllVisibility();
        });
        
        chip.setTag("search");
        chipGroup.addView(chip);
        updateClearAllVisibility();
    }

    private void removeCategoryFilter() {
        removeChipByTag("category");
    }

    private void removePriceFilter() {
        removeChipByTag("price");
    }

    private void removeSearchFilter() {
        removeChipByTag("search");
    }

    private void removeChipByTag(String tag) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (tag.equals(chip.getTag())) {
                chipGroup.removeView(chip);
                break;
            }
        }
    }

    private void updateClearAllVisibility() {
        if (chipGroup.getChildCount() > 0) {
            showClearAllChip();
        } else {
            removeClearAllChip();
        }
    }

    private void showClearAllChip() {
        if (findClearAllChip() == null) {
            Chip clearAllChip = new Chip(getContext());
            clearAllChip.setText(R.string.clear_all_filters);
            clearAllChip.setCloseIconVisible(false);
            clearAllChip.setChipBackgroundColorResource(R.color.clearAllChipBackground);
            clearAllChip.setOnClickListener(v -> clearAllFilters());
            clearAllChip.setTag("clear_all");
            chipGroup.addView(clearAllChip, 0);
        }
    }

    private void removeClearAllChip() {
        Chip clearAllChip = findClearAllChip();
        if (clearAllChip != null) {
            chipGroup.removeView(clearAllChip);
        }
    }

    private Chip findClearAllChip() {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if ("clear_all".equals(chip.getTag())) {
                return chip;
            }
        }
        return null;
    }

    public void clearAllFilters() {
        chipGroup.removeAllViews();
        if (listener != null) {
            listener.onAllFiltersRemoved();
        }
    }

    public void setOnFilterRemovedListener(OnFilterRemovedListener listener) {
        this.listener = listener;
    }

    public boolean hasActiveFilters() {
        return chipGroup.getChildCount() > 0;
    }
}