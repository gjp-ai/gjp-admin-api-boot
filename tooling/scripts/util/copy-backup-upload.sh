#!/bin/bash
# Script to sync upload files from OneDrive backup to local Data directory
# This script will merge the directories and keep original files (no overwrite)

SOURCE_DIR="$HOME/Library/CloudStorage/OneDrive-Personal/Backup/gjp/upload/"
DEST_DIR="$HOME/Data/gjp-api/upload"

echo "Syncing GJP upload files..."
echo "Source: $SOURCE_DIR"
echo "Destination: $DEST_DIR"

# Ensure destination directory exists
mkdir -p "$DEST_DIR"

# Check if source directory exists
if [ ! -d "$SOURCE_DIR" ]; then
    echo "Error: Source directory $SOURCE_DIR does not exist."
    exit 1
fi

# Use rsync to merge directories
# -a: archive mode (preserves permissions, times, symb-links, etc.)
# -v: verbose
# --ignore-existing: Do not overwrite files that already exist in the destination folder
rsync -av --ignore-existing "$SOURCE_DIR" "$DEST_DIR"

echo "Sync completed successfully."
