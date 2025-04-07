package com.example.shop.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.shop.models.Product;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

@Dao
public interface ProductDao {
    @Insert
    long insert(Product product);

    @Update
    int update(Product product);

    @Query("UPDATE products SET is_favorite = :isFavorite WHERE id = :productId")
    int updateFavoriteStatus(long productId, boolean isFavorite);

    @Query("UPDATE products SET quantity_in_stock = :quantity WHERE id = :productId")
    int updateStock(long productId, int quantity);

    @Query("UPDATE products SET rating = ((rating * rating_count) + :newRating) / (rating_count + 1), " +
           "rating_count = rating_count + 1 WHERE id = :productId")
    void addRating(long productId, float newRating);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM products WHERE id = :productId")
    Product findById(long productId);

    @Query("SELECT * FROM products WHERE category_id = :categoryId ORDER BY name ASC")
    List<Product> findByCategory(long categoryId);

    @Query("SELECT * FROM products WHERE is_favorite = 1")
    List<Product> getFavorites();

    @Query("SELECT MIN(price) FROM products")
    double getMinPrice();

    @Query("SELECT MAX(price) FROM products")
    double getMaxPrice();

    @Query("SELECT COUNT(*) FROM products WHERE " +
           "(:query IS NULL OR (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%')) AND " +
           "(:categoryId IS NULL OR category_id = :categoryId) AND " +
           "(:minPrice IS NULL OR price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR price <= :maxPrice)")
    int getCount(String query, Long categoryId, Double minPrice, Double maxPrice);

    @Query("SELECT COUNT(*) FROM products WHERE category_id = :categoryId")
    int getCountInCategory(long categoryId);
}