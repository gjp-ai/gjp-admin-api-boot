# CMS Module — Database Schema

## Common Conventions

- All primary keys are `CHAR(36)` UUIDs, set by the application (not `AUTO_INCREMENT`).
- All tables use `utf8mb4` charset / `utf8mb4_unicode_ci` collation.
- Soft delete is implemented via `is_active TINYINT(1)`, never via physical deletion.
- Audit columns (`created_at`, `updated_at`, `created_by`, `updated_by`) are inherited from `BaseEntity` and present on every table.
- `lang` is an `ENUM('EN','ZH')` on all tables, defaulting to `'EN'`.
- `display_order INT DEFAULT 0` is present on all tables as a client-side sort hint.

---

## Table: `cms_article`

Stores multilingual articles with optional cover image and source attribution.

| Column | Type | Nullable | Default | Notes |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NOT NULL | — | Primary key (UUID) |
| `title` | `VARCHAR(255)` | NOT NULL | — | Article headline |
| `summary` | `VARCHAR(500)` | NULL | — | Short description / teaser |
| `content` | `LONGTEXT` | NULL | — | Full article body (HTML or Markdown) |
| `original_url` | `VARCHAR(500)` | NULL | — | Source URL if imported |
| `source_name` | `VARCHAR(255)` | NULL | — | Source publication name |
| `cover_image_filename` | `VARCHAR(500)` | NULL | — | Filename of the stored cover image |
| `cover_image_original_url` | `VARCHAR(500)` | NULL | — | Original URL of the cover image |
| `tags` | `VARCHAR(500)` | NULL | — | Comma-separated tag list |
| `lang` | `ENUM('EN','ZH')` | NOT NULL | `'EN'` | Content language |
| `display_order` | `INT` | NOT NULL | `0` | Sort order |
| `is_active` | `TINYINT(1)` | NOT NULL | `1` | Soft-delete flag |
| `created_at` | `DATETIME` | NOT NULL | — | Auto-set on insert |
| `updated_at` | `DATETIME` | NOT NULL | — | Auto-updated on update |
| `created_by` | `CHAR(36)` | NULL | — | userId of creator |
| `updated_by` | `CHAR(36)` | NULL | — | userId of last updater |

**Indexes:** `PRIMARY KEY (id)`, recommended index on `(lang, is_active, display_order)`.

---

## Table: `cms_article_image`

Stores body images linked to an article (not the cover — the cover lives in `cms_article`).

| Column | Type | Nullable | Default | Notes |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NOT NULL | — | Primary key (UUID) |
| `article_id` | `CHAR(36)` | NOT NULL | — | References `cms_article.id` |
| `article_title` | `VARCHAR(500)` | NULL | — | Denormalised article title (for display) |
| `filename` | `VARCHAR(255)` | NOT NULL | — | Stored image filename |
| `original_url` | `VARCHAR(500)` | NULL | — | Source URL if downloaded |
| `width` | `INT` | NULL | — | Image width in pixels |
| `height` | `INT` | NULL | — | Image height in pixels |
| `lang` | `ENUM('EN','ZH')` | NOT NULL | `'EN'` | Content language |
| `display_order` | `INT` | NOT NULL | `0` | Sort order within the article |
| `is_active` | `TINYINT(1)` | NOT NULL | `1` | Soft-delete flag |
| `created_at` | `DATETIME` | NOT NULL | — | |
| `updated_at` | `DATETIME` | NOT NULL | — | |
| `created_by` | `CHAR(36)` | NULL | — | |
| `updated_by` | `CHAR(36)` | NULL | — | |

**Indexes:** `PRIMARY KEY (id)`, `INDEX (article_id)`.

> Note: No JPA `@ManyToOne` FK constraint is defined on `article_id`. Referential integrity is enforced at the application layer.

---

## Table: `cms_audio`

Stores audio files. Supports both direct file upload and async URL/YouTube download.

| Column | Type | Nullable | Default | Notes |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NOT NULL | — | Primary key (UUID) |
| `name` | `VARCHAR(255)` | NOT NULL | — | Display name |
| `filename` | `VARCHAR(255)` | NULL | — | Stored audio filename (NULL while downloading) |
| `size_bytes` | `BIGINT` | NULL | — | File size in bytes |
| `cover_image_filename` | `VARCHAR(500)` | NULL | — | Thumbnail/cover image filename |
| `original_url` | `VARCHAR(500)` | NULL | — | Source URL |
| `source_name` | `VARCHAR(255)` | NULL | — | Source platform or publication |
| `description` | `VARCHAR(500)` | NULL | — | Short description |
| `subtitle` | `TEXT` | NULL | — | Full subtitle / transcript |
| `artist` | `VARCHAR(255)` | NULL | — | Artist or speaker name |
| `tags` | `VARCHAR(500)` | NULL | — | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NOT NULL | `'EN'` | |
| `display_order` | `INT` | NOT NULL | `0` | |
| `is_active` | `TINYINT(1)` | NOT NULL | `1` | |
| `download_status` | `ENUM('PENDING','DOWNLOADING','COMPLETED','FAILED')` | NULL | — | Async download lifecycle state |
| `download_error` | `VARCHAR(500)` | NULL | — | Error message if `FAILED` (truncated at 500 chars) |
| `created_at` | `DATETIME` | NOT NULL | — | |
| `updated_at` | `DATETIME` | NOT NULL | — | |
| `created_by` | `CHAR(36)` | NULL | — | |
| `updated_by` | `CHAR(36)` | NULL | — | |

**Download lifecycle:** `NULL` (direct upload) → `PENDING` → `DOWNLOADING` → `COMPLETED` / `FAILED`.

---

## Table: `cms_file`

Stores generic file assets (PDFs, ZIPs, documents, etc.).

| Column | Type | Nullable | Default | Notes |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NOT NULL | — | Primary key (UUID) |
| `name` | `VARCHAR(255)` | NULL | — | Display name |
| `original_url` | `VARCHAR(255)` | NULL | — | Source URL if downloaded |
| `source_name` | `VARCHAR(255)` | NULL | — | Source attribution |
| `filename` | `VARCHAR(255)` | NULL | — | Stored filename |
| `size_bytes` | `BIGINT` | NULL | — | File size in bytes |
| `extension` | `VARCHAR(255)` | NULL | — | File extension (e.g., `pdf`, `zip`) |
| `mime_type` | `VARCHAR(255)` | NULL | — | MIME type (e.g., `application/pdf`) |
| `tags` | `VARCHAR(255)` | NULL | — | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NOT NULL | `'EN'` | |
| `display_order` | `INT` | NULL | `0` | |
| `is_active` | `TINYINT(1)` | NULL | `1` | |
| `created_at` | `DATETIME` | NOT NULL | — | |
| `updated_at` | `DATETIME` | NOT NULL | — | |
| `created_by` | `CHAR(36)` | NULL | — | |
| `updated_by` | `CHAR(36)` | NULL | — | |

---

## Table: `cms_image`

Stores images with auto-generated thumbnails and dimension metadata.

| Column | Type | Nullable | Default | Notes |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NOT NULL | — | Primary key (UUID) |
| `name` | `VARCHAR(255)` | NOT NULL | — | Display name |
| `original_url` | `VARCHAR(500)` | NULL | — | Source URL if downloaded |
| `source_name` | `VARCHAR(255)` | NULL | — | Source attribution |
| `filename` | `VARCHAR(255)` | NOT NULL | — | Full-size image filename |
| `thumbnail_filename` | `VARCHAR(255)` | NULL | — | Auto-generated thumbnail filename |
| `extension` | `VARCHAR(10)` | NOT NULL | — | File extension (e.g., `jpg`, `png`) |
| `mime_type` | `VARCHAR(100)` | NULL | — | MIME type |
| `size_bytes` | `BIGINT` | NULL | — | Full-size file size in bytes |
| `width` | `INT` | NULL | — | Full-size width in pixels |
| `height` | `INT` | NULL | — | Full-size height in pixels |
| `alt_text` | `VARCHAR(500)` | NULL | — | Accessibility alt text |
| `tags` | `VARCHAR(500)` | NULL | — | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NOT NULL | `'EN'` | |
| `display_order` | `INT` | NOT NULL | `0` | |
| `is_active` | `TINYINT(1)` | NOT NULL | `1` | |
| `created_at` | `DATETIME` | NOT NULL | — | |
| `updated_at` | `DATETIME` | NOT NULL | — | |
| `created_by` | `CHAR(36)` | NULL | — | |
| `updated_by` | `CHAR(36)` | NULL | — | |

---

## Table: `cms_logo`

Stores logos. All uploaded or URL-fetched logos are automatically resized to the configured target size (default 256px longest side). SVGs are stored as-is; raster formats are resized via Thumbnailator. Filename is auto-generated from the logo name using Pinyin4j for Chinese characters.

| Column | Type | Nullable | Default | Notes |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NOT NULL | — | Primary key (UUID) |
| `name` | `VARCHAR(255)` | NOT NULL | — | Display name (also drives filename generation) |
| `original_url` | `VARCHAR(500)` | NULL | — | Source URL if downloaded |
| `filename` | `VARCHAR(255)` | NOT NULL | — | Stored file (auto-generated: `name_256px_timestamp.ext`) |
| `extension` | `VARCHAR(16)` | NOT NULL | — | File extension: `svg`, `png`, `jpg`, `gif`, `webp` |
| `tags` | `VARCHAR(500)` | NULL | — | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NOT NULL | `'EN'` | |
| `display_order` | `INT` | NOT NULL | `0` | |
| `is_active` | `TINYINT(1)` | NOT NULL | `1` | |
| `created_at` | `DATETIME` | NOT NULL | — | |
| `updated_at` | `DATETIME` | NOT NULL | — | |
| `created_by` | `CHAR(36)` | NULL | — | |
| `updated_by` | `CHAR(36)` | NULL | — | |

**Filename pattern:** `{name_as_snake_case}_{targetSize}_{yyyyMMddHHmmss}.{ext}`
Example: `"My Logo"` → `my_logo_256_20260404120000.png`

---

## Table: `cms_question`

Stores FAQ-style Q&A pairs.

| Column | Type | Nullable | Default | Notes |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NOT NULL | — | Primary key (UUID) |
| `question` | `VARCHAR(255)` | NOT NULL | — | The question text |
| `answer` | `VARCHAR(2000)` | NULL | — | The answer text |
| `tags` | `VARCHAR(500)` | NULL | — | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NOT NULL | `'EN'` | |
| `display_order` | `INT` | NOT NULL | `0` | |
| `is_active` | `TINYINT(1)` | NOT NULL | `1` | |
| `created_at` | `DATETIME` | NOT NULL | — | |
| `updated_at` | `DATETIME` | NOT NULL | — | |
| `created_by` | `CHAR(36)` | NULL | — | |
| `updated_by` | `CHAR(36)` | NULL | — | |

---

## Table: `cms_video`

Stores videos. Supports direct upload and async download from URLs and YouTube via yt-dlp.

| Column | Type | Nullable | Default | Notes |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NOT NULL | — | Primary key (UUID) |
| `name` | `VARCHAR(255)` | NOT NULL | — | Display name |
| `filename` | `VARCHAR(255)` | NULL | — | Stored video filename (NULL while downloading) |
| `size_bytes` | `BIGINT` | NULL | — | File size in bytes |
| `cover_image_filename` | `VARCHAR(500)` | NULL | — | Thumbnail/cover image filename |
| `original_url` | `VARCHAR(500)` | NULL | — | Source URL (YouTube or direct link) |
| `source_name` | `VARCHAR(255)` | NULL | — | Source platform name |
| `description` | `VARCHAR(500)` | NULL | — | Short description |
| `tags` | `VARCHAR(500)` | NULL | — | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NOT NULL | `'EN'` | |
| `display_order` | `INT` | NOT NULL | `0` | |
| `is_active` | `TINYINT(1)` | NOT NULL | `1` | |
| `download_status` | `ENUM('PENDING','DOWNLOADING','COMPLETED','FAILED')` | NULL | — | Async download lifecycle state |
| `download_error` | `VARCHAR(500)` | NULL | — | Error message if `FAILED` (truncated at 500 chars) |
| `created_at` | `DATETIME` | NOT NULL | — | |
| `updated_at` | `DATETIME` | NOT NULL | — | |
| `created_by` | `CHAR(36)` | NULL | — | |
| `updated_by` | `CHAR(36)` | NULL | — | |

**Download lifecycle:** `NULL` (direct upload) → `PENDING` → `DOWNLOADING` → `COMPLETED` / `FAILED`.

---

## Table: `cms_website`

Stores a directory of external websites with name, URL, and optional logo.

| Column | Type | Nullable | Default | Notes |
|---|---|---|---|---|
| `id` | `CHAR(36)` | NOT NULL | — | Primary key (UUID) |
| `name` | `VARCHAR(128)` | NOT NULL | — | Website display name |
| `url` | `VARCHAR(500)` | NOT NULL | — | Website URL |
| `logo_url` | `VARCHAR(500)` | NULL | — | URL or path to website logo |
| `description` | `VARCHAR(1000)` | NULL | — | Short description |
| `tags` | `VARCHAR(500)` | NULL | — | Comma-separated tags |
| `lang` | `ENUM('EN','ZH')` | NOT NULL | `'EN'` | |
| `display_order` | `INT` | NOT NULL | `0` | |
| `is_active` | `TINYINT(1)` | NOT NULL | `1` | |
| `created_at` | `DATETIME` | NOT NULL | — | |
| `updated_at` | `DATETIME` | NOT NULL | — | |
| `created_by` | `CHAR(36)` | NULL | — | |
| `updated_by` | `CHAR(36)` | NULL | — | |

---

## Entity Relationship Summary

```
cms_article (1) ──────────────── (N) cms_article_image
                                      article_id → cms_article.id
                                      (app-enforced, no DB FK)

cms_audio      ─ standalone
cms_file       ─ standalone
cms_image      ─ standalone
cms_logo       ─ standalone
cms_question   ─ standalone
cms_video      ─ standalone
cms_website    ─ standalone
```

All CMS tables are independent except `cms_article_image`, which logically references `cms_article` via `article_id`.

---

*Last Updated: 2026-04-04*
