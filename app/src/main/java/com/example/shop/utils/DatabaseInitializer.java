package com.example.shop.utils;

import android.content.Context;
import com.example.shop.db.ShopDatabase;
import com.example.shop.db.dao.CategoryDao;
import com.example.shop.models.Category;

public class DatabaseInitializer {
    /**
     * Initialize PC part categories in the database
     * @param context Android context
     */
    public static void initializeCategories(Context context) {
        ShopDatabase db = ShopDatabase.getInstance(context);
        CategoryDao categoryDao = new CategoryDao(db);

        // Initialize all PC part categories
        String[] categories = {
            ImageManager.CATEGORY_PROCESSORS,
            ImageManager.CATEGORY_GRAPHICS_CARDS,
            ImageManager.CATEGORY_MOTHERBOARDS,
            ImageManager.CATEGORY_MEMORY,
            ImageManager.CATEGORY_STORAGE,
            ImageManager.CATEGORY_POWER_SUPPLIES,
            ImageManager.CATEGORY_PERIPHERALS,
            ImageManager.CATEGORY_ACCESSORIES
        };

        // Add each category if it doesn't already exist
        for (String categorySlug : categories) {
            String displayName = ImageManager.getCategoryDisplayName(categorySlug);
            
            // Create new category with display name
            Category category = new Category(displayName);
            
            // Insert only if category with this name doesn't exist
            if (!categoryExists(categoryDao, displayName)) {
                categoryDao.insert(category);
            }
        }
    }

    private static boolean categoryExists(CategoryDao dao, String categoryName) {
        for (Category category : dao.getAll()) {
            if (category.getName().equals(categoryName)) {
                return true;
            }
        }
        return false;
    }
}