package com.example.shop.repositories;

import androidx.annotation.NonNull;
import com.example.shop.db.dao.CartDao;
import com.example.shop.db.dao.CategoryDao;
import com.example.shop.db.dao.ProductDao;
import com.example.shop.models.CartItem;
import com.example.shop.models.Category;
import com.example.shop.models.FilterState;
import com.example.shop.models.Product;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;

public class ProductRepository {
    private final ProductDao productDao;
    private final CartDao cartDao;
    private final CategoryDao categoryDao;

    public ProductRepository(@NonNull ProductDao productDao, 
                           @NonNull CartDao cartDao,
                           @NonNull CategoryDao categoryDao) {
        this.productDao = productDao;
        this.cartDao = cartDao;
        this.categoryDao = categoryDao;
    }

    @NonNull
    public Single<List<Product>> getProducts(@NonNull FilterState filterState) {
        return Single.fromCallable(() -> 
            productDao.getAll(
                filterState.getQuery(),
                filterState.getCategoryId(),
                filterState.getMinPrice(),
                filterState.getMaxPrice(),
                filterState.getSortOption()
            )
        );
    }

    @NonNull
    public Single<List<Category>> getCategories() {
        return Single.fromCallable(categoryDao::getAll);
    }

    @NonNull
    public Single<Category> getCategory(long categoryId) {
        return Single.fromCallable(() -> categoryDao.findById(categoryId));
    }

    @NonNull
    public Single<String> getCategoryName(long categoryId) {
        return Single.fromCallable(() -> {
            Category category = categoryDao.findById(categoryId);
            return category != null ? category.getName() : "";
        });
    }

    @NonNull
    public Single<Product> getProduct(long productId) {
        return Single.fromCallable(() -> 
            productDao.findById(productId)
        );
    }

    @NonNull
    public Completable addToCart(@NonNull Product product) {
        return Completable.fromAction(() -> {
            CartItem existingItem = cartDao.findByProductId(product.getId());
            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                cartDao.update(existingItem);
            } else {
                CartItem newItem = new CartItem(product.getId(), 1);
                cartDao.insert(newItem);
            }
        });
    }

    @NonNull
    public Completable toggleFavorite(@NonNull Product product) {
        return Completable.fromAction(() -> {
            product.setFavorite(!product.isFavorite());
            productDao.update(product);
        });
    }

    @NonNull
    public Single<List<Product>> getFavoriteProducts() {
        return Single.fromCallable(productDao::getFavorites);
    }

    @NonNull
    public Single<List<Product>> getProductsByCategory(long categoryId) {
        return Single.fromCallable(() -> productDao.findByCategory(categoryId));
    }

    @NonNull
    public Single<Double> getMinProductPrice() {
        return Single.fromCallable(productDao::getMinPrice);
    }

    @NonNull
    public Single<Double> getMaxProductPrice() {
        return Single.fromCallable(productDao::getMaxPrice);
    }

    @NonNull
    public Single<Integer> getProductCount(@NonNull FilterState filterState) {
        return Single.fromCallable(() ->
            productDao.getCount(
                filterState.getQuery(),
                filterState.getCategoryId(),
                filterState.getMinPrice(),
                filterState.getMaxPrice()
            )
        );
    }

    @NonNull
    public Single<Integer> getProductCountInCategory(long categoryId) {
        return Single.fromCallable(() -> productDao.getCountInCategory(categoryId));
    }

    /**
     * Updates product information in the database.
     * @param product The product to update
     * @return Completable that completes when the update is done
     */
    @NonNull
    public Completable updateProduct(@NonNull Product product) {
        return Completable.fromAction(() ->
            productDao.update(product)
        );
    }

    /**
     * Updates category information in the database.
     * @param category The category to update
     * @return Completable that completes when the update is done
     */
    @NonNull
    public Completable updateCategory(@NonNull Category category) {
        return Completable.fromAction(() ->
            categoryDao.update(category)
        );
    }

    /**
     * Refreshes the product and category cache if needed.
     * This could be used to sync with a remote data source in the future.
     */
    @NonNull
    public Completable refreshData() {
        // TODO: Implement remote data source sync
        return Completable.complete();
    }
}