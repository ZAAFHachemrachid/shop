package com.example.shop.utils;

import android.content.Context;
import android.util.Log;
import com.example.shop.db.ShopDatabase;
import com.example.shop.db.dao.CategoryDao;
import com.example.shop.db.dao.ProductDao;
import com.example.shop.models.Category;
import com.example.shop.models.Product;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Random;

public class ProductInitializer {
    private static final String TAG = "ProductInitializer";
    private static final String SOURCE_DIR = "/home/rachid/imageget/pc_part_images";
    private static Random random = new Random();
    
    /**
     * Initialize products with images from the specified directory
     */
    public static void initializeProductsWithImages(Context context) {
        ShopDatabase db = ShopDatabase.getInstance(context);
        ProductDao productDao = new ProductDao(db);
        CategoryDao categoryDao = new CategoryDao(db);
        
        // First, initialize categories
        DatabaseInitializer.initializeCategories(context);
        
        // Get all categories
        List<Category> categories = categoryDao.getAll();
        
        // Clear existing products
        clearExistingProducts(productDao);
        
        // Process source directory
        File sourceDir = new File(SOURCE_DIR);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            Log.e(TAG, "Source directory not found: " + sourceDir.getPath());
            return;
        }
        
        // Get all image files
        File[] imageFiles = sourceDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".jpg") || 
            name.toLowerCase().endsWith(".jpeg") || 
            name.toLowerCase().endsWith(".png")
        );
        
        if (imageFiles == null || imageFiles.length == 0) {
            Log.e(TAG, "No image files found");
            return;
        }
        
        // Ensure destination directories exist
        createDestinationDirectories(context);
        
        // Process each image
        int counter = 1;
        for (File imageFile : imageFiles) {
            try {
                // Get random category
                Category category = categories.get(random.nextInt(categories.size()));
                String categorySlug = getCategorySlug(category.getName());
                
                // Create new filename
                String ext = getFileExtension(imageFile);
                String newFileName = String.format("%s_%03d.%s", categorySlug, counter, ext);
                
                // Copy image to destination
                File destDir = new File(context.getFilesDir(), 
                    "drawable-nodpi/products/pc_parts/" + categorySlug);
                File destFile = new File(destDir, newFileName);
                
                copyFile(imageFile, destFile);
                
                // Create product
                String productName = generateProductName(imageFile.getName());
                String imagePath = "drawable-nodpi/products/pc_parts/" + categorySlug + "/" + newFileName;
                
                Product product = new Product(
                    productName,
                    "High-quality " + category.getName().toLowerCase(),
                    generateRandomPrice(),
                    imagePath,
                    category.getId()
                );
                
                // Set additional properties
                product.setOriginalPrice(product.getPrice() * 1.2); // 20% markup
                product.setQuantityInStock(random.nextInt(50) + 1);
                product.setRating(3.5f + (float)(random.nextDouble() * 1.5));
                product.setRatingCount(random.nextInt(100));
                
                // Insert product
                productDao.insert(product);
                Log.d(TAG, "Added product: " + productName + " with image: " + imagePath);
                
                counter++;
            } catch (IOException e) {
                Log.e(TAG, "Error processing image: " + imageFile.getName(), e);
            }
        }
        
        Log.i(TAG, "Finished initializing " + (counter-1) + " products");
    }
    
    private static void clearExistingProducts(ProductDao productDao) {
        List<Product> existingProducts = productDao.getAll(null, null, null, null, null);
        for (Product product : existingProducts) {
            productDao.delete(product.getId());
        }
    }
    
    private static void createDestinationDirectories(Context context) {
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
        
        for (String category : categories) {
            File dir = new File(context.getFilesDir(), 
                "drawable-nodpi/products/pc_parts/" + category);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }
    
    private static String generateProductName(String filename) {
        // Remove extension and convert to title case
        String name = filename.substring(0, filename.lastIndexOf('.'))
            .replace('_', ' ')
            .replace('-', ' ');
            
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;

        for (char c : name.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
            titleCase.append(c);
        }
        
        return titleCase.toString();
    }
    
    private static String getCategorySlug(String categoryName) {
        switch (categoryName) {
            case "Processors": return ImageManager.CATEGORY_PROCESSORS;
            case "Graphics Cards": return ImageManager.CATEGORY_GRAPHICS_CARDS;
            case "Motherboards": return ImageManager.CATEGORY_MOTHERBOARDS;
            case "Memory": return ImageManager.CATEGORY_MEMORY;
            case "Storage Devices": return ImageManager.CATEGORY_STORAGE;
            case "Power Supplies": return ImageManager.CATEGORY_POWER_SUPPLIES;
            case "Peripherals": return ImageManager.CATEGORY_PERIPHERALS;
            case "Accessories": return ImageManager.CATEGORY_ACCESSORIES;
            default: return ImageManager.CATEGORY_ACCESSORIES;
        }
    }
    
    private static void copyFile(File source, File dest) throws IOException {
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        
        try (FileChannel inputChannel = new FileInputStream(source).getChannel();
             FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }
    
    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return name.substring(lastDotIndex + 1).toLowerCase();
        }
        return "jpg"; // default extension
    }
    
    private static double generateRandomPrice() {
        // Generate random price between $50 and $2000
        return 50.0 + random.nextDouble() * 1950.0;
    }
}