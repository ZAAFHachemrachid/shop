package com.example.shop.utils;

import android.content.Context;
import java.io.File;

public class ImageManager {
    private static final String BASE_PATH = "drawable-nodpi/products/pc_parts";
    
    // Category constants
    public static final String CATEGORY_PROCESSORS = "processors";
    public static final String CATEGORY_GRAPHICS_CARDS = "graphics_cards";
    public static final String CATEGORY_MOTHERBOARDS = "motherboards";
    public static final String CATEGORY_MEMORY = "memory";
    public static final String CATEGORY_STORAGE = "storage";
    public static final String CATEGORY_POWER_SUPPLIES = "power_supplies";
    public static final String CATEGORY_PERIPHERALS = "peripherals";
    public static final String CATEGORY_ACCESSORIES = "accessories";

    /**
     * Generate the relative path for a product image
     * @param category the product category (use category constants)
     * @param imageName the name of the image file
     * @return relative path suitable for database storage
     */
    public static String generateImagePath(String category, String imageName) {
        return String.format("%s/%s/%s", BASE_PATH, category, imageName);
    }

    /**
     * Get the display name for a category
     * @param categorySlug the category slug (use category constants)
     * @return human readable category name
     */
    public static String getCategoryDisplayName(String categorySlug) {
        switch (categorySlug) {
            case CATEGORY_PROCESSORS:
                return "Processors";
            case CATEGORY_GRAPHICS_CARDS:
                return "Graphics Cards";
            case CATEGORY_MOTHERBOARDS:
                return "Motherboards";
            case CATEGORY_MEMORY:
                return "Memory";
            case CATEGORY_STORAGE:
                return "Storage Devices";
            case CATEGORY_POWER_SUPPLIES:
                return "Power Supplies";
            case CATEGORY_PERIPHERALS:
                return "Peripherals";
            case CATEGORY_ACCESSORIES:
                return "Accessories";
            default:
                return "Unknown Category";
        }
    }

    /**
     * Check if the given category is valid
     * @param category the category to validate
     * @return true if category is valid
     */
    public static boolean isValidCategory(String category) {
        return category != null && (
            category.equals(CATEGORY_PROCESSORS) ||
            category.equals(CATEGORY_GRAPHICS_CARDS) ||
            category.equals(CATEGORY_MOTHERBOARDS) ||
            category.equals(CATEGORY_MEMORY) ||
            category.equals(CATEGORY_STORAGE) ||
            category.equals(CATEGORY_POWER_SUPPLIES) ||
            category.equals(CATEGORY_PERIPHERALS) ||
            category.equals(CATEGORY_ACCESSORIES)
        );
    }

    /**
     * Generate a sequential filename for a new image in a category
     * @param category the product category
     * @param extension the file extension (e.g., "jpg", "png")
     * @param context Android context to access resources
     * @return generated filename like "processors_001.jpg"
     */
    public static String generateSequentialImageName(String category, String extension, Context context) {
        if (!isValidCategory(category)) {
            throw new IllegalArgumentException("Invalid category: " + category);
        }

        File resourceDir = new File(context.getFilesDir(), BASE_PATH + "/" + category);
        if (!resourceDir.exists()) {
            return String.format("%s_001.%s", category, extension);
        }

        File[] existingFiles = resourceDir.listFiles((dir, name) -> 
            name.startsWith(category) && name.endsWith("." + extension)
        );
        
        int maxNumber = 0;
        if (existingFiles != null) {
            for (File file : existingFiles) {
                try {
                    String numStr = file.getName().replaceAll("[^0-9]", "");
                    int num = Integer.parseInt(numStr);
                    maxNumber = Math.max(maxNumber, num);
                } catch (NumberFormatException e) {
                    // Skip files that don't match our naming pattern
                }
            }
        }

        return String.format("%s_%03d.%s", category, maxNumber + 1, extension);
    }
}