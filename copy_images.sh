#!/bin/bash

# Source and destination directories
SRC_DIR="/home/rachid/imageget/pc_part_images"
DEST_BASE_DIR="app/src/main/res/drawable-nodpi/products/pc_parts"

# Create destination directories if they don't exist
mkdir -p "$DEST_BASE_DIR"/{processors,graphics_cards,motherboards,memory,storage,power_supplies,peripherals,accessories}

# Function to clean filename
clean_filename() {
    echo "$1" | tr '[:upper:]' '[:lower:]' | sed 's/[^a-z0-9.]/_/g'
}

# Copy images with sequential numbering
counter=1
for img in "$SRC_DIR"/*; do
    if [[ -f "$img" ]]; then
        # Get file extension
        ext="${img##*.}"
        # Clean and format filename
        new_name=$(printf "%03d.%s" "$counter" "$ext")
        
        # Randomly select a category directory
        categories=(processors graphics_cards motherboards memory storage power_supplies peripherals accessories)
        category=${categories[$RANDOM % ${#categories[@]}]}
        
        # Copy file with new name
        cp "$img" "$DEST_BASE_DIR/$category/${category}_$new_name"
        echo "Copied $img to $DEST_BASE_DIR/$category/${category}_$new_name"
        
        ((counter++))
    fi
done

echo "Finished copying $((counter-1)) images"