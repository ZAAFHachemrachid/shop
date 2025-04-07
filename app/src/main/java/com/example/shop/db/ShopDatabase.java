package com.example.shop.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.shop.db.dao.CartDao;
import com.example.shop.db.dao.CategoryDao;
import com.example.shop.db.dao.ProductDao;
import com.example.shop.db.dao.UserDao;
import com.example.shop.models.Cart;
import com.example.shop.models.Category;
import com.example.shop.models.Product;
import com.example.shop.models.User;

@Database(
    entities = {Product.class, Category.class, Cart.class, User.class},
    version = 3
)
@TypeConverters({DateConverters.class})
public abstract class ShopDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "shop.db";
    private static volatile ShopDatabase instance;

    public abstract ProductDao productDao();
    public abstract CategoryDao categoryDao();
    public abstract CartDao cartDao();
    public abstract UserDao userDao();

    public static synchronized ShopDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    ShopDatabase.class,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}