#!/bin/bash
# =============================================================================
# restore_db_upload_folder.sh
# Restores gjp_db from a SQL backup and copies the upload folder from OneDrive.
# =============================================================================

set -euo pipefail

# ---------------------------------------------------------------------------
# Configuration
# ---------------------------------------------------------------------------
DB_NAME="gjp_db"
BACKUP_SQL="${HOME}/Library/CloudStorage/OneDrive-Personal/Backup/gjp/mysql/backup_gjp_db_2026-04-25_143316.sql"
BACKUP_UPLOAD_DIR="${HOME}/Library/CloudStorage/OneDrive-Personal/Backup/gjp/upload"
TARGET_UPLOAD_DIR="${HOME}/Data/gjp-api/upload"

# ---------------------------------------------------------------------------
# Validate environment variables
# ---------------------------------------------------------------------------
if [[ -z "${MYSQL_USERNAME:-}" ]]; then
  echo "❌  Error: MYSQL_USERNAME environment variable is not set." >&2
  exit 1
fi

if [[ -z "${MYSQL_PASSWORD:-}" ]]; then
  echo "❌  Error: MYSQL_PASSWORD environment variable is not set." >&2
  exit 1
fi

# ---------------------------------------------------------------------------
# Validate source files / directories
# ---------------------------------------------------------------------------
if [[ ! -f "${BACKUP_SQL}" ]]; then
  echo "❌  Error: SQL backup file not found: ${BACKUP_SQL}" >&2
  exit 1
fi

if [[ ! -d "${BACKUP_UPLOAD_DIR}" ]]; then
  echo "❌  Error: Upload backup directory not found: ${BACKUP_UPLOAD_DIR}" >&2
  exit 1
fi

# ---------------------------------------------------------------------------
# Step 1 – Restore the database
# ---------------------------------------------------------------------------
echo "============================================================"
echo "Step 1: Restoring database '${DB_NAME}' from SQL backup …"
echo "  Source : ${BACKUP_SQL}"
echo "============================================================"

# Drop and recreate the database, then import the dump
mysql -u "${MYSQL_USERNAME}" -p"${MYSQL_PASSWORD}" <<SQL
DROP DATABASE IF EXISTS \`${DB_NAME}\`;
CREATE DATABASE \`${DB_NAME}\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SQL

mysql -u "${MYSQL_USERNAME}" -p"${MYSQL_PASSWORD}" "${DB_NAME}" < "${BACKUP_SQL}"

echo "✅  Database '${DB_NAME}' restored successfully."
echo ""

# ---------------------------------------------------------------------------
# Step 2 – Copy the upload folder
# ---------------------------------------------------------------------------
echo "============================================================"
echo "Step 2: Copying upload folder …"
echo "  Source : ${BACKUP_UPLOAD_DIR}"
echo "  Target : ${TARGET_UPLOAD_DIR}"
echo "============================================================"

# Create the target parent directory if it does not exist
mkdir -p "$(dirname "${TARGET_UPLOAD_DIR}")"

# Copy the entire upload directory, preserving timestamps and permissions
# Using rsync so that large folders are handled efficiently and progress is shown
if command -v rsync &>/dev/null; then
  rsync -ah --progress --delete "${BACKUP_UPLOAD_DIR}/" "${TARGET_UPLOAD_DIR}/"
else
  # Fallback to cp if rsync is unavailable
  rm -rf "${TARGET_UPLOAD_DIR}"
  cp -R "${BACKUP_UPLOAD_DIR}" "${TARGET_UPLOAD_DIR}"
fi

echo "✅  Upload folder copied successfully."
echo ""
echo "🎉  Restore completed."
