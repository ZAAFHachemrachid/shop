package com.example.shop.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import com.example.shop.models.User;
import com.example.shop.db.ShopDatabase;

public class UserDao {
    private final SQLiteDatabase db;

    public UserDao(ShopDatabase database) {
        this.db = database.getWritableDatabase();
    }

    public long insert(User user) {
        ContentValues values = new ContentValues();
        values.put(ShopDatabase.COLUMN_EMAIL, user.getEmail());
        values.put(ShopDatabase.COLUMN_NAME, user.getName());
        values.put(ShopDatabase.COLUMN_PASSWORD_HASH, user.getPasswordHash());

        return db.insert(ShopDatabase.TABLE_USERS, null, values);
    }

    public User findByEmail(String email) {
        Cursor cursor = db.query(
            ShopDatabase.TABLE_USERS,
            new String[]{
                ShopDatabase.COLUMN_ID,
                ShopDatabase.COLUMN_EMAIL,
                ShopDatabase.COLUMN_NAME,
                ShopDatabase.COLUMN_PASSWORD_HASH,
                ShopDatabase.COLUMN_CREATED_AT
            },
            ShopDatabase.COLUMN_EMAIL + " = ?",
            new String[]{email},
            null,
            null,
            null
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getLong(0));
            user.setEmail(cursor.getString(1));
            user.setName(cursor.getString(2));
            user.setPasswordHash(cursor.getString(3));
            user.setCreatedAt(cursor.getString(4));
        }
        cursor.close();
        return user;
    }

    public User findById(long id) {
        Cursor cursor = db.query(
            ShopDatabase.TABLE_USERS,
            new String[]{
                ShopDatabase.COLUMN_ID,
                ShopDatabase.COLUMN_EMAIL,
                ShopDatabase.COLUMN_NAME,
                ShopDatabase.COLUMN_PASSWORD_HASH,
                ShopDatabase.COLUMN_CREATED_AT
            },
            ShopDatabase.COLUMN_ID + " = ?",
            new String[]{String.valueOf(id)},
            null,
            null,
            null
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getLong(0));
            user.setEmail(cursor.getString(1));
            user.setName(cursor.getString(2));
            user.setPasswordHash(cursor.getString(3));
            user.setCreatedAt(cursor.getString(4));
        }
        cursor.close();
        return user;
    }

    public int update(User user) {
        ContentValues values = new ContentValues();
        values.put(ShopDatabase.COLUMN_EMAIL, user.getEmail());
        values.put(ShopDatabase.COLUMN_NAME, user.getName());
        if (user.getPasswordHash() != null) {
            values.put(ShopDatabase.COLUMN_PASSWORD_HASH, user.getPasswordHash());
        }

        return db.update(
            ShopDatabase.TABLE_USERS,
            values,
            ShopDatabase.COLUMN_ID + " = ?",
            new String[]{String.valueOf(user.getId())}
        );
    }

    public boolean emailExists(String email) {
        Cursor cursor = db.query(
            ShopDatabase.TABLE_USERS,
            new String[]{ShopDatabase.COLUMN_ID},
            ShopDatabase.COLUMN_EMAIL + " = ?",
            new String[]{email},
            null,
            null,
            null
        );
        
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}