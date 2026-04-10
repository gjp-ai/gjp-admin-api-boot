#!/bin/bash
# ============================================================================
# Clean Open API Integrated Code
# ============================================================================
#
# Removes the 'api/open' directory created by integrate-open-api.sh.
# Use this script to return the repository to its original state or to 
# ensure a clean start before re-integrating.
#
# Usage: ./clean-open-api.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ADMIN_ROOT="$SCRIPT_DIR/../../.."

TARGET="$ADMIN_ROOT/src/main/java/org/ganjp/api/open"

echo "=========================================="
echo "  Removing merged open-api code"
echo "=========================================="
echo "  Target: $TARGET"
echo ""

if [ ! -d "$TARGET" ]; then
    echo "  [SKIP] Directory does not exist: $TARGET"
    echo "  Nothing to remove."
    echo "=========================================="
    exit 0
fi

echo "  The following will be permanently deleted:"
find "$TARGET" -type d | sed 's|^|    |'
echo ""

# Prompt for confirmation before deleting
read -p "  Are you sure you want to delete $TARGET? [y/N] " confirm
case "$confirm" in
    [yY][eE][sS]|[yY])
        rm -rf "$TARGET"
        echo ""
        echo "  [DONE] Removed: $TARGET"
        ;;
    *)
        echo "  [CANCELLED] Nothing was deleted."
        ;;
esac

echo "=========================================="
