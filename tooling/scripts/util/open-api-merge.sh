#!/bin/bash
# ============================================================================
# Merge gjp-open-api-boot public endpoints into gjp-admin-api-boot
# ============================================================================
#
# Copies open API controllers, services, and response DTOs into
# org.ganjp.api.open.{cms,master} within gjp-admin-api-boot.
#
# Reuses admin's existing: entities, repositories, properties, CmsUtil,
# ApiResponse, PaginatedResponse, and GlobalExceptionHandler.
#
# What gets created:
#   src/main/java/org/ganjp/api/open/cms/{article,audio,video,image,file,logo,question,website}/
#   src/main/java/org/ganjp/api/open/master/setting/
#
# Usage: ./open-api-merge.sh
#        ./open-api-merge.sh --remove   (undo the merge)

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ADMIN_ROOT="$SCRIPT_DIR/../../.."
OPEN_ROOT="$ADMIN_ROOT/../gjp-open-api-boot"

SRC="$OPEN_ROOT/src/main/java/org/ganjp/api"
DEST="$ADMIN_ROOT/src/main/java/org/ganjp/api/open"

# ── Handle --remove flag ────────────────────────────────────────────────────
if [[ "${1:-}" == "--remove" ]]; then
    echo "=========================================="
    echo "  Removing merged open-api code"
    echo "=========================================="
    if [ -d "$DEST" ]; then
        rm -rf "$DEST"
        echo "  [DONE] Removed: $DEST"
    else
        echo "  [SKIP] Nothing to remove."
    fi
    echo "=========================================="
    exit 0
fi

echo "=========================================="
echo "  Merging gjp-open-api-boot → admin"
echo "=========================================="
echo "  Source : $SRC"
echo "  Target : $DEST"
echo ""

# ── Verify source exists ────────────────────────────────────────────────────
if [ ! -d "$SRC" ]; then
    echo "  [ERROR] Source not found: $SRC"
    exit 1
fi

# ── Clean previous merge ────────────────────────────────────────────────────
if [ -d "$DEST" ]; then
    echo "  [CLEAN] Removing previous merge..."
    rm -rf "$DEST"
fi

# ── Helper: insert imports after the package declaration ─────────────────────
insert_imports() {
    local file="$1"
    shift

    local pkg_line
    pkg_line=$(grep -n '^package ' "$file" | head -1 | cut -d: -f1)

    {
        head -n "$pkg_line" "$file"
        echo ""
        for imp in "$@"; do
            echo "import ${imp};"
        done
        tail -n +"$((pkg_line + 1))" "$file"
    } > "${file}.tmp" && mv "${file}.tmp" "$file"
}

# ── Helper: apply common transforms to all copied files ──────────────────────
transform_common() {
    local file="$1"
    # Fix package declarations
    sed -i '' 's/^package org\.ganjp\.api\.cms\./package org.ganjp.api.open.cms./' "$file"
    sed -i '' 's/^package org\.ganjp\.api\.master\./package org.ganjp.api.open.master./' "$file"
    # Fix core→common model imports
    sed -i '' 's/import org\.ganjp\.api\.core\.model\./import org.ganjp.api.common.model./' "$file"
    # Fix CmsUtil import
    sed -i '' 's/import org\.ganjp\.api\.cms\.util\.CmsUtil/import org.ganjp.api.common.util.CmsUtil/' "$file"
}

# ── Helper: copy, transform, and wire up a CMS module ───────────────────────
# Usage: merge_module <module> <entity_class> <repo_class> [props_class] [extra_imports...]
merge_module() {
    local module="$1"
    local entity_pkg="$2"       # e.g. org.ganjp.api.cms.article
    local entity_class="$3"     # e.g. Article
    local repo_class="$4"       # e.g. ArticleRepository
    local props_class="${5:-}"   # e.g. ArticleProperties (optional)
    shift 5 2>/dev/null || shift $#

    local src_dir="$SRC/cms/$module"
    local dest_dir="$DEST/cms/$module"

    if [ ! -d "$src_dir" ]; then
        echo "  [SKIP] $module not found"
        return
    fi

    echo "  [COPY] cms/$module → open/cms/$module"
    mkdir -p "$dest_dir"

    # Copy controllers, services, and response DTOs (skip entities, repos, properties)
    for f in "$src_dir"/*.java; do
        local basename
        basename=$(basename "$f")
        # Skip entity, repository, and properties/config files
        case "$basename" in
            "${entity_class}.java"|*Repository.java|*Properties.java|*UploadProperties.java|*Config.java)
                continue ;;
        esac
        cp "$f" "$dest_dir/"
    done

    # Apply common transforms
    for f in "$dest_dir"/*.java; do
        [ -f "$f" ] || continue
        transform_common "$f"
    done

    # Add entity imports to controllers (for Entity.Language references)
    for f in "$dest_dir"/*Controller.java; do
        [ -f "$f" ] || continue
        insert_imports "$f" "${entity_pkg}.${entity_class}"
        # Add explicit bean name to avoid conflicts
        local ctrl_name
        ctrl_name=$(basename "$f" .java)
        local bean_name="open${ctrl_name}"
        sed -i '' "s/^@RestController$/@RestController(\"${bean_name}\")/" "$f"
    done

    # Add entity/repo/props imports to services and set explicit bean name
    for f in "$dest_dir"/*Service.java; do
        [ -f "$f" ] || continue
        local svc_imports=("${entity_pkg}.${entity_class}" "${entity_pkg}.${repo_class}")
        if [ -n "$props_class" ]; then
            svc_imports+=("${entity_pkg}.${props_class}")
        fi
        insert_imports "$f" "${svc_imports[@]}"
        local svc_name
        svc_name=$(basename "$f" .java)
        local bean_name="open${svc_name}"
        sed -i '' "s/^@Service$/@Service(\"${bean_name}\")/" "$f"
    done

    # Add entity import to response DTOs that don't already have it
    for f in "$dest_dir"/*Response.java "$dest_dir"/*DetailResponse.java; do
        [ -f "$f" ] || continue
        if ! grep -q "import ${entity_pkg}.${entity_class};" "$f"; then
            insert_imports "$f" "${entity_pkg}.${entity_class}"
        fi
    done
}

# ═══════════════════════════════════════════════════════════════════════════════
# MERGE EACH MODULE
# ═══════════════════════════════════════════════════════════════════════════════

# 1. Article
merge_module "article" "org.ganjp.api.cms.article" "Article" "ArticleRepository" "ArticleProperties"

# 2. Audio
merge_module "audio" "org.ganjp.api.cms.audio" "Audio" "AudioRepository" "AudioUploadProperties"

# 3. Video
merge_module "video" "org.ganjp.api.cms.video" "Video" "VideoRepository" "VideoUploadProperties"

# 4. Image
merge_module "image" "org.ganjp.api.cms.image" "Image" "ImageRepository" "ImageUploadProperties"

# 5. Logo
merge_module "logo" "org.ganjp.api.cms.logo" "Logo" "LogoRepository" "LogoUploadProperties"

# 6. Question
merge_module "question" "org.ganjp.api.cms.question" "Question" "QuestionRepository"

# 7. Website
merge_module "website" "org.ganjp.api.cms.website" "Website" "WebsiteRepository"

# ── 8. File module (special: entity is File in open API → FileAsset in admin) ──
echo "  [COPY] cms/file → open/cms/file"
mkdir -p "$DEST/cms/file"
for f in "$SRC/cms/file/"*.java; do
    basename=$(basename "$f")
    case "$basename" in
        File.java|FileRepository.java|FileUploadProperties.java) continue ;;
    esac
    cp "$f" "$DEST/cms/file/"
done

for f in "$DEST/cms/file/"*.java; do
    [ -f "$f" ] || continue
    transform_common "$f"
    # Replace FQN entity references: org.ganjp.api.cms.file.File → FileAsset
    sed -i '' 's/org\.ganjp\.api\.cms\.file\.File\.Language/FileAsset.Language/g' "$f"
    sed -i '' 's/org\.ganjp\.api\.cms\.file\.File/FileAsset/g' "$f"
done

# Fix unqualified File.Language in FileResponse
sed -i '' 's/File\.Language/FileAsset.Language/g' "$DEST/cms/file/FileResponse.java"

# Add imports and bean names
for f in "$DEST/cms/file/"*Controller.java; do
    [ -f "$f" ] || continue
    insert_imports "$f" "org.ganjp.api.cms.file.FileAsset"
    sed -i '' 's/^@RestController$/@RestController("openFileController")/' "$f"
done

for f in "$DEST/cms/file/"*Service.java; do
    [ -f "$f" ] || continue
    insert_imports "$f" \
        "org.ganjp.api.cms.file.FileAsset" \
        "org.ganjp.api.cms.file.FileRepository" \
        "org.ganjp.api.cms.file.FileUploadProperties"
    sed -i '' 's/^@Service$/@Service("openFileService")/' "$f"
done

for f in "$DEST/cms/file/"*Response.java; do
    [ -f "$f" ] || continue
    if ! grep -q "import org.ganjp.api.cms.file.FileAsset;" "$f"; then
        insert_imports "$f" "org.ganjp.api.cms.file.FileAsset"
    fi
done

# ── 9. Master/Setting module ────────────────────────────────────────────────
echo "  [COPY] master/setting → open/master/setting"
mkdir -p "$DEST/master/setting"
cp "$SRC/master/setting/AppSettingController.java" "$DEST/master/setting/"
cp "$SRC/master/setting/AppSettingService.java"    "$DEST/master/setting/"
cp "$SRC/master/setting/AppSettingDto.java"        "$DEST/master/setting/"

for f in "$DEST/master/setting/"*.java; do
    transform_common "$f"
done

insert_imports "$DEST/master/setting/AppSettingService.java" \
    "org.ganjp.api.master.setting.AppSetting" \
    "org.ganjp.api.master.setting.AppSettingRepository"

insert_imports "$DEST/master/setting/AppSettingDto.java" \
    "org.ganjp.api.master.setting.AppSetting"

sed -i '' 's/^@RestController$/@RestController("openAppSettingController")/' \
    "$DEST/master/setting/AppSettingController.java"
sed -i '' 's/^@Service$/@Service("openAppSettingService")/' \
    "$DEST/master/setting/AppSettingService.java"

# ═══════════════════════════════════════════════════════════════════════════════
# SUMMARY
# ═══════════════════════════════════════════════════════════════════════════════
echo ""
echo "  Merge complete!"
echo ""
echo "  Files created under: $DEST"
find "$DEST" -name "*.java" | wc -l | xargs -I{} echo "  Total Java files: {}"
echo ""
echo "=========================================="
