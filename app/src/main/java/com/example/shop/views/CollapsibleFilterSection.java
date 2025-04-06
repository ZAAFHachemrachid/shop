package com.example.shop.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.shop.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class CollapsibleFilterSection extends MaterialCardView {
    private LinearLayout contentContainer;
    private MaterialButton toggleButton;
    private TextView titleView;
    private ImageView expandIcon;
    private boolean isExpanded = true;
    private int originalHeight = 0;

    public CollapsibleFilterSection(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CollapsibleFilterSection(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CollapsibleFilterSection(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Inflate the main layout
        LayoutInflater.from(context).inflate(R.layout.view_collapsible_filter_section, this, true);

        // Initialize views
        contentContainer = findViewById(R.id.contentContainer);
        toggleButton = findViewById(R.id.toggleButton);
        titleView = findViewById(R.id.sectionTitle);
        expandIcon = findViewById(R.id.expandIcon);

        // Set up click listener for the toggle button
        toggleButton.setOnClickListener(v -> toggle());

        // Initially expanded
        isExpanded = true;
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setTitle(int titleResId) {
        titleView.setText(titleResId);
    }

    public void toggle() {
        if (originalHeight == 0) {
            originalHeight = contentContainer.getHeight();
        }

        ValueAnimator valueAnimator;
        if (isExpanded) {
            // Collapse
            valueAnimator = ValueAnimator.ofInt(contentContainer.getHeight(), 0);
            expandIcon.setRotation(0f);
        } else {
            // Expand
            valueAnimator = ValueAnimator.ofInt(0, originalHeight);
            expandIcon.setRotation(180f);
        }

        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = contentContainer.getLayoutParams();
            layoutParams.height = value;
            contentContainer.setLayoutParams(layoutParams);
        });

        valueAnimator.start();
        isExpanded = !isExpanded;
    }

    public void addContent(View view) {
        contentContainer.addView(view);
    }

    public void clearContent() {
        contentContainer.removeAllViews();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        if (expanded != isExpanded) {
            toggle();
        }
    }
}