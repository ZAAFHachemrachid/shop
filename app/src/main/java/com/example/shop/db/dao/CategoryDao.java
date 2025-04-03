package com.example.shop.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.shop.db.ShopDatabase;
import com.example.shop.models.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryDao {
    private ShopDatabase dbHelper;

    public CategoryDao(ShopDatabase dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long insert(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ShopDatabase.COLUMN_NAME, category.getName());
        
        long id = db.insert(ShopDatabase.TABLE_CATEGORIES, null, values);
        category.setId(id);
        return id;
    }

    public int update(Category category) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ShopDatabase.COLUMN_NAME, category.getName());
        
        return db.update(ShopDatabase.TABLE_CATEGORIES, values,
                ShopDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(category.getId())});
    }

    public void delete(long categoryId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(ShopDatabase.TABLE_CATEGORIES,
                ShopDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(categoryId)});
    }

    public Category get(long categoryId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(ShopDatabase.TABLE_CATEGORIES,
                null,
                ShopDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(categoryId)},
                null, null, null);

        Category category = null;
        if (cursor != null && cursor.moveToFirst()) {
            category = createFromCursor(cursor);
            cursor.close();
        }
        return category;
    }

    public List<Category> getAll() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Cursor cursor = db.query(ShopDatabase.TABLE_CATEGORIES,
                null, null, null, null, null,
                ShopDatabase.COLUMN_NAME + " ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                categories.add(createFromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return categories;
    }

    private Category createFromCursor(Cursor cursor) {
        return new Category(
                cursor.getLong(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(ShopDatabase.COLUMN_CREATED_AT))
        );
    }
}