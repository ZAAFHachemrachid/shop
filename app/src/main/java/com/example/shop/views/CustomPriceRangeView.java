package com.example.shop.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.shop.R;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

public class CustomPriceRangeView extends ConstraintLayout {
    private RangeSlider rangeSlider;
    private TextInputLayout minPriceLayout;
    private TextInputLayout maxPriceLayout;
    private TextInputEditText minPriceInput;
    private TextInputEditText maxPriceInput;
    
    private final NumberFormat currencyFormat;
    private OnPriceRangeChangedListener listener;
    private boolean isUpdating = false;

    public interface OnPriceRangeChangedListener {
        void onPriceRangeChanged(float minPrice, float maxPrice);
    }

    public CustomPriceRangeView(Context context) {
        this(context, null);
    }

    public CustomPriceRangeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomPriceRangeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("fr", "DZ"));
        currencyFormat.setCurrency(Currency.getInstance("DZD"));
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_custom_price_range, this, true);
        
        rangeSlider = findViewById(R.id.rangeSlider);
        minPriceLayout = findViewById(R.id.minPriceLayout);
        maxPriceLayout = findViewById(R.id.maxPriceLayout);
        minPriceInput = findViewById(R.id.minPriceInput);
        maxPriceInput = findViewById(R.id.maxPriceInput);

        setupRangeSlider();
        setupPriceInputs();
    }

    private void setupRangeSlider() {
        rangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (!isUpdating && fromUser) {
                isUpdating = true;
                List<Float> values = slider.getValues();
                updatePriceInputs(values.get(0), values.get(1));
                notifyPriceRangeChanged(values.get(0), values.get(1));
                isUpdating = false;
            }
        });
    }

    private void setupPriceInputs() {
        TextWatcher priceWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!isUpdating) {
                    validateAndUpdatePrices();
                }
            }
        };

        minPriceInput.addTextChangedListener(priceWatcher);
        maxPriceInput.addTextChangedListener(priceWatcher);
    }

    private void validateAndUpdatePrices() {
        try {
            isUpdating = true;
            
            String minStr = minPriceInput.getText().toString();
            String maxStr = maxPriceInput.getText().toString();
            
            if (minStr.isEmpty() || maxStr.isEmpty()) {
                return;
            }

            float minPrice = Float.parseFloat(minStr);
            float maxPrice = Float.parseFloat(maxStr);

            if (minPrice > maxPrice) {
                maxPriceLayout.setError(getContext().getString(R.string.price_validation_error));
                return;
            }

            maxPriceLayout.setError(null);
            rangeSlider.setValues(minPrice, maxPrice);
            notifyPriceRangeChanged(minPrice, maxPrice);
            
        } catch (NumberFormatException e) {
            // Invalid number format, ignore
        } finally {
            isUpdating = false;
        }
    }

    private void updatePriceInputs(float minPrice, float maxPrice) {
        minPriceInput.setText(String.valueOf(Math.round(minPrice)));
        maxPriceInput.setText(String.valueOf(Math.round(maxPrice)));
    }

    private void notifyPriceRangeChanged(float minPrice, float maxPrice) {
        if (listener != null) {
            listener.onPriceRangeChanged(minPrice, maxPrice);
        }
    }

    public void setOnPriceRangeChangedListener(OnPriceRangeChangedListener listener) {
        this.listener = listener;
    }

    public void setPriceRange(float minPrice, float maxPrice) {
        isUpdating = true;
        rangeSlider.setValues(minPrice, maxPrice);
        updatePriceInputs(minPrice, maxPrice);
        isUpdating = false;
    }

    public void setValueRange(float min, float max) {
        rangeSlider.setValueFrom(min);
        rangeSlider.setValueTo(max);
    }

    public float getMinPrice() {
        return rangeSlider.getValues().get(0);
    }

    public float getMaxPrice() {
        return rangeSlider.getValues().get(1);
    }
}