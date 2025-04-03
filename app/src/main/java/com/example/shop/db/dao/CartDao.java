package com.example.shop.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.shop.db.ShopDatabase;
import com.example.shop.models.CartItem;
import com.example.shop.models.Product;
import java.util.ArrayList;
import java.util.List;

public class CartDao {
    private ShopDatabase dbHelper;
    private ProductDao productDao;

    public CartDao(ShopDatabase dbHelper) {
        this.dbHelper = dbHelper;
        this.productDao = new ProductDao(dbHelper);
    }

    public long insert(CartItem cartItem) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ShopDatabase.COLUMN_PRODUCT_ID, cartItem.getProductId());
        values.put(ShopDatabase.COLUMN_QUANTITY, cartItem.getQuantity());

        long id = db.insert(ShopDatabase.TABLE_CART, null, values);
        cartItem.setId(id);
        return id;
    }

    public int update(CartItem cartItem) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ShopDatabase.COLUMN_QUANTITY, cartItem.getQuantity());

        return db.update(ShopDatabase.TABLE_CART, values,
                ShopDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(cartItem.getId())});
    }

    public void delete(long cartItemId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ShopDatabase.TABLE_CART,
                ShopDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(cartItemId)});
    }

    public void clearCart() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ShopDatabase.TABLE_CART, null, null);
    }

    public CartItem get(long cartItemId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ShopDatabase.TABLE_CART,
                null,
                ShopDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(cartItemId)},
                null, null, null);

        CartItem cartItem = null;
        if (cursor != null && cursor.moveToFirst()) {
            cartItem = createFromCursor(cursor);
            cursor.close();
        }
        return cartItem;
    }

    public List<CartItem> getAll() {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(ShopDatabase.TABLE_CART,
                null, null, null, null, null,
                ShopDatabase.COLUMN_CREATED_AT + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                CartItem item = createFromCursor(cursor);
                // Load associated product
                Product product = productDao.get(item.getProductId());
                item.setProduct(product);
                cartItems.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return cartItems;
    }

    public CartItem findByProductId(long productId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ShopDatabase.TABLE_CART,
                null,
                ShopDatabase.COLUMN_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(productId)},
                null, null, null);

        CartItem cartItem = null;
        if (cursor != null && cursor.moveToFirst()) {
            cartItem = createFromCursor(cursor);
            cursor.close();
        }
        return cartItem;
    }

    public double getCartTotal() {
        double total = 0;
        List<CartItem> items = getAll();
        for (CartItem item : items) {
            if (item.getProduct() != null) {
                total += item.getSubtotal();
            }
        }
        return total;
    }

    private CartItem createFromCursor(Cursor cursor) {
        return new CartItem(
                cursor.getLong(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_PRODUCT_ID)),
                cursor.getInt(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_QUANTITY)),
                cursor.getString(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_CREATED_AT))
        );
    }
}