package com.example.shop.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Transaction;

import com.example.shop.models.Cart;
import com.example.shop.models.Product;

import java.util.List;

@Dao
public interface CartDao {
    @Insert
    long insert(Cart cartItem);

    @Update
    int update(Cart cartItem);

    @Delete
    void delete(Cart cartItem);

    @Query("DELETE FROM cart WHERE product_id = :productId")
    void deleteByProductId(long productId);

    @Query("SELECT * FROM cart WHERE id = :cartId")
    Cart findById(long cartId);

    @Query("SELECT * FROM cart WHERE product_id = :productId")
    Cart findByProductId(long productId);

    @Query("SELECT * FROM cart ORDER BY created_at DESC")
    List<Cart> getAll();

    @Query("SELECT COUNT(*) FROM cart")
    int getCount();

    @Query("SELECT SUM(c.quantity * p.price) FROM cart c JOIN products p ON c.product_id = p.id")
    double getTotalPrice();

    @Query("UPDATE cart SET quantity = :quantity WHERE product_id = :productId")
    void updateQuantity(long productId, int quantity);

    @Query("DELETE FROM cart")
    void clear();

    @Transaction
    @Query("SELECT p.* FROM products p INNER JOIN cart c ON p.id = c.product_id")
    List<Product> getCartProducts();
}