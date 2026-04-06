#!/bin/bash
#
# GJP API Admin - Create CMS Upload Directories
# ================================================
# Creates all required upload directories for the CMS modules.
#
# Uses CMS_UPLOAD_DIR env var if set, otherwise defaults to ~/Data/gjp-api/upload
# (matching application.yml: cms.upload.root-directory)
#
# Usage:
#   ./init-upload-folders.sh              # create dirs under default root
#   CMS_UPLOAD_DIR=/data ./init-upload-folders.sh  # create dirs under custom root

set -euo pipefail

ROOT="${CMS_UPLOAD_DIR:-$HOME/Data/gjp-api/upload}"

DIRS=(
    "$ROOT/articles/cover-images"
    "$ROOT/articles/cover-images/deleted"
    "$ROOT/articles/content-images"
    "$ROOT/articles/content-images/deleted"
    "$ROOT/audios"
    "$ROOT/audios/deleted"
    "$ROOT/audios/cover-images"
    "$ROOT/audios/cover-images/deleted"
    "$ROOT/videos"
    "$ROOT/videos/deleted"
    "$ROOT/videos/cover-images"
    "$ROOT/videos/cover-images/deleted"
    "$ROOT/images"
    "$ROOT/images/deleted"
    "$ROOT/files"
    "$ROOT/files/deleted"
    "$ROOT/logos"
    "$ROOT/logos/deleted"
)

echo ""
echo "============================================"
echo "  CMS Upload Directory Setup"
echo "============================================"
echo ""
echo "Root: $ROOT"
echo ""

for dir in "${DIRS[@]}"; do
    if [[ -d "$dir" ]]; then
        echo "  ✓ exists   $dir"
    else
        mkdir -p "$dir"
        echo "  ✚ created  $dir"
    fi
done

echo ""
echo "Done. All CMS upload directories are ready."
echo ""
