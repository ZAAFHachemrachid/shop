package com.example.shop.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ShopDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "shop.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_CART = "cart";

    // Common Column Names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_CREATED_AT = "created_at";

    // Products Table Columns
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_CATEGORY_ID = "category_id";

    // Cart Table Columns
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_QUANTITY = "quantity";

    // Create Table Statements
    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES +
            "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " TEXT NOT NULL," +
            COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
            ")";

    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS +
            "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " TEXT NOT NULL," +
            COLUMN_DESCRIPTION + " TEXT," +
            COLUMN_PRICE + " REAL NOT NULL," +
            COLUMN_IMAGE + " TEXT," +
            COLUMN_CATEGORY_ID + " INTEGER," +
            COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY(" + COLUMN_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORIES + "(" + COLUMN_ID + ")" +
            ")";

    private static final String CREATE_TABLE_CART = "CREATE TABLE " + TABLE_CART +
            "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_PRODUCT_ID + " INTEGER NOT NULL," +
            COLUMN_QUANTITY + " INTEGER NOT NULL DEFAULT 1," +
            COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
            "FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_ID + ")" +
            ")";

    private static ShopDatabase instance;

    private ShopDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized ShopDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new ShopDatabase(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_CART);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        
        // Create tables again
        onCreate(db);
    }
}