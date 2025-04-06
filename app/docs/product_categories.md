# PC Parts Categories

## Main Categories
1. Processors (CPUs)
2. Graphics Cards (GPUs)
3. Motherboards
4. Memory (RAM)
5. Storage Devices
6. Power Supplies
7. Peripherals
8. Accessories

## Directory Structure
```
app/src/main/res/drawable-nodpi/products/
└── pc_parts/
    ├── processors/
    ├── graphics_cards/
    ├── motherboards/
    ├── memory/
    ├── storage/
    ├── power_supplies/
    ├── peripherals/
    └── accessories/
```

## Image Naming Convention
- Format: `category_name_sequential_number.jpg`
- Examples:
  - processors_001.jpg
  - graphics_cards_001.jpg
  - motherboards_001.jpg
  etc.

## Implementation Plan
1. Create these category directories
2. Analyze each image from the source directory
3. Categorize images into appropriate directories
4. Update database with new image paths