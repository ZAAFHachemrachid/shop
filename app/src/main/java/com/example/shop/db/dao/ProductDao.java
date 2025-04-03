package com.example.shop.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.shop.db.ShopDatabase;
import com.example.shop.models.Product;
import com.example.shop.models.SortOption;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {
    private ShopDatabase dbHelper;

    public ProductDao(ShopDatabase dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insert(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ShopDatabase.COLUMN_NAME, product.getName());
        values.put(ShopDatabase.COLUMN_DESCRIPTION, product.getDescription());
        values.put(ShopDatabase.COLUMN_PRICE, product.getPrice());
        values.put(ShopDatabase.COLUMN_IMAGE, product.getImage());
        values.put(ShopDatabase.COLUMN_CATEGORY_ID, product.getCategoryId());

        long id = db.insert(ShopDatabase.TABLE_PRODUCTS, null, values);
        product.setId(id);
        return id;
    }

    public int update(Product product) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ShopDatabase.COLUMN_NAME, product.getName());
        values.put(ShopDatabase.COLUMN_DESCRIPTION, product.getDescription());
        values.put(ShopDatabase.COLUMN_PRICE, product.getPrice());
        values.put(ShopDatabase.COLUMN_IMAGE, product.getImage());
        values.put(ShopDatabase.COLUMN_CATEGORY_ID, product.getCategoryId());

        return db.update(ShopDatabase.TABLE_PRODUCTS, values,
                ShopDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(product.getId())});
    }

    public void delete(long productId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ShopDatabase.TABLE_PRODUCTS,
                ShopDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(productId)});
    }

    public Product get(long productId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ShopDatabase.TABLE_PRODUCTS,
                null,
                ShopDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(productId)},
                null, null, null);

        Product product = null;
        if (cursor != null && cursor.moveToFirst()) {
            product = createFromCursor(cursor);
            cursor.close();
        }
        return product;
    }

    public List<Product> getAll(String query, Long categoryId,
                              Double minPrice, Double maxPrice,
                              SortOption sortOption) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<String> whereConditions = new ArrayList<>();
        List<String> whereArgs = new ArrayList<>();

        // Search query condition
        if (query != null && !query.trim().isEmpty()) {
            whereConditions.add("(" + ShopDatabase.COLUMN_NAME + " LIKE ? OR " +
                              ShopDatabase.COLUMN_DESCRIPTION + " LIKE ?)");
            whereArgs.add("%" + query + "%");
            whereArgs.add("%" + query + "%");
        }

        // Category filter
        if (categoryId != null) {
            whereConditions.add(ShopDatabase.COLUMN_CATEGORY_ID + " = ?");
            whereArgs.add(String.valueOf(categoryId));
        }

        // Price range filter
        if (minPrice != null) {
            whereConditions.add(ShopDatabase.COLUMN_PRICE + " >= ?");
            whereArgs.add(String.valueOf(minPrice));
        }
        if (maxPrice != null) {
            whereConditions.add(ShopDatabase.COLUMN_PRICE + " <= ?");
            whereArgs.add(String.valueOf(maxPrice));
        }

        String selection = whereConditions.isEmpty() ? null :
            String.join(" AND ", whereConditions);

        String orderBy = sortOption != null ? sortOption.getSqlOrder() :
            ShopDatabase.COLUMN_CREATED_AT + " DESC";

        Cursor cursor = db.query(ShopDatabase.TABLE_PRODUCTS,
                null,
                selection,
                whereArgs.toArray(new String[0]),
                null,
                null,
                orderBy);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                products.add(createFromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return products;
    }

    public List<Product> getByCategory(long categoryId) {
        List<Product> products = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(ShopDatabase.TABLE_PRODUCTS,
                null,
                ShopDatabase.COLUMN_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(categoryId)},
                null, null,
                ShopDatabase.COLUMN_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                products.add(createFromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return products;
    }

    private Product createFromCursor(Cursor cursor) {
        return new Product(
                cursor.getLong(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_DESCRIPTION)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_PRICE)),
                cursor.getString(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_IMAGE)),
                cursor.getLong(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_CATEGORY_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_CREATED_AT))
        );
    }
}