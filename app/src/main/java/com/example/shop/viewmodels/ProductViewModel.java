package com.example.shop.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.shop.models.Category;
import com.example.shop.models.FilterState;
import com.example.shop.models.Product;
import com.example.shop.models.ProductUiState;
import com.example.shop.repositories.ProductRepository;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.List;

public class ProductViewModel extends ViewModel {
    private final ProductRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    
    private final MutableLiveData<ProductUiState> uiState = new MutableLiveData<>(new ProductUiState.Loading());
    private final MutableLiveData<FilterState> filterState = new MutableLiveData<>(new FilterState());
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    
    public ProductViewModel(@NonNull ProductRepository repository) {
        this.repository = repository;
    }
    
    @NonNull
    public LiveData<ProductUiState> getUiState() {
        return uiState;
    }
    
    @NonNull
    public LiveData<FilterState> getFilterState() {
        return filterState;
    }

    @NonNull
    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public void loadCategories() {
        disposables.add(
            repository.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    categoriesList -> categories.setValue(categoriesList),
                    error -> uiState.setValue(new ProductUiState.Error("Failed to load categories", error))
                )
        );
    }
    
    public void loadProducts() {
        FilterState currentFilters = filterState.getValue();
        if (currentFilters == null) {
            currentFilters = new FilterState();
        }
        
        disposables.add(
            repository.getProducts(currentFilters)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(__ -> uiState.setValue(new ProductUiState.Loading()))
                .subscribe(
                    products -> uiState.setValue(new ProductUiState.Success(products)),
                    error -> uiState.setValue(new ProductUiState.Error("Failed to load products", error))
                )
        );
    }
    
    public void updateFilters(@NonNull FilterState newFilters) {
        filterState.setValue(newFilters);
        loadProducts();
    }
    
    public void clearFilters() {
        filterState.setValue(new FilterState());
        loadProducts();
    }
    
    public void addToCart(@NonNull Product product) {
        disposables.add(
            repository.addToCart(product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    () -> {}, // Success - no UI update needed
                    error -> uiState.setValue(new ProductUiState.Error("Failed to add to cart", error))
                )
        );
    }
    
    public void toggleFavorite(@NonNull Product product) {
        disposables.add(
            repository.toggleFavorite(product)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    () -> loadProducts(), // Reload products to show updated favorite status
                    error -> uiState.setValue(new ProductUiState.Error("Failed to update favorite", error))
                )
        );
    }

    public LiveData<String> getCategoryName(long categoryId) {
        MutableLiveData<String> categoryName = new MutableLiveData<>();
        disposables.add(
            repository.getCategoryName(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    categoryName::setValue,
                    error -> uiState.setValue(new ProductUiState.Error("Failed to get category name", error))
                )
        );
        return categoryName;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }

    public static class Factory implements ViewModelProvider.Factory {
        private final ProductRepository repository;

        public Factory(ProductRepository repository) {
            this.repository = repository;
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ProductViewModel.class)) {
                return (T) new ProductViewModel(repository);
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}