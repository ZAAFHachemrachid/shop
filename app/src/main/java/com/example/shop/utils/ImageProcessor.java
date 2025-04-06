package com.example.shop.utils;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import android.util.Log;

public class ImageProcessor {
    private static final String TAG = "ImageProcessor";
    
    /**
     * Copy an image file to the appropriate category directory
     * @param context Android context
     * @param sourceFile source image file
     * @param category target category (use ImageManager category constants)
     * @return the relative path of the copied image or null if failed
     */
    public static String processImage(Context context, File sourceFile, String category) {
        if (!ImageManager.isValidCategory(category)) {
            Log.e(TAG, "Invalid category: " + category);
            return null;
        }

        try {
            // Get file extension
            String extension = getFileExtension(sourceFile);
            if (extension == null) {
                Log.e(TAG, "Invalid file extension for: " + sourceFile.getName());
                return null;
            }

            // Generate new filename
            String newFileName = ImageManager.generateSequentialImageName(category, extension, context);
            
            // Create target directory if it doesn't exist
            File targetDir = new File(context.getFilesDir(), 
                "drawable-nodpi/products/pc_parts/" + category);
            if (!targetDir.exists()) {
                if (!targetDir.mkdirs()) {
                    Log.e(TAG, "Failed to create directory: " + targetDir.getPath());
                    return null;
                }
            }

            // Create target file
            File targetFile = new File(targetDir, newFileName);
            
            // Copy file
            copyFile(sourceFile, targetFile);
            
            // Return relative path for database storage
            return ImageManager.generateImagePath(category, newFileName);

        } catch (IOException e) {
            Log.e(TAG, "Error processing image: " + e.getMessage());
            return null;
        }
    }

    /**
     * Copy a file using NIO for better performance
     */
    private static void copyFile(File source, File target) throws IOException {
        try (FileChannel inputChannel = new FileInputStream(source).getChannel();
             FileChannel outputChannel = new FileOutputStream(target).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }

    /**
     * Get the file extension from a file
     * @return the extension without dot, or null if invalid
     */
    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
            return name.substring(lastDotIndex + 1).toLowerCase();
        }
        return null;
    }

    /**
     * Process multiple images in bulk
     * @param context Android context
     * @param sourceDir directory containing source images
     * @param category target category
     * @return array of processed image paths, or null if failed
     */
    public static String[] processImagesInDirectory(Context context, File sourceDir, String category) {
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            Log.e(TAG, "Invalid source directory: " + sourceDir.getPath());
            return null;
        }

        File[] imageFiles = sourceDir.listFiles((dir, name) -> 
            name.toLowerCase().endsWith(".jpg") || 
            name.toLowerCase().endsWith(".jpeg") || 
            name.toLowerCase().endsWith(".png")
        );

        if (imageFiles == null || imageFiles.length == 0) {
            Log.e(TAG, "No image files found in: " + sourceDir.getPath());
            return null;
        }

        String[] processedPaths = new String[imageFiles.length];
        for (int i = 0; i < imageFiles.length; i++) {
            processedPaths[i] = processImage(context, imageFiles[i], category);
        }

        return processedPaths;
    }
}