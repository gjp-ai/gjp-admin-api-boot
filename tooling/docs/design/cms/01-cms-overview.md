# CMS Module — Architecture Overview

## Purpose

The CMS (Content Management System) module provides storage, retrieval, and serving of content assets for the GJP platform. It manages nine entity types across eight sub-modules, all protected by role-based access control and built on the shared `BaseEntity` infrastructure.

---

## Sub-Modules

| Sub-module | Entity | Table | Description |
|---|---|---|---|
| `article` | `Article` + `ArticleImage` | `cms_article`, `cms_article_image` | Multilingual long-form articles with associated images and cover photo |
| `audio` | `Audio` | `cms_audio` | Audio files, supports direct upload and async URL/YouTube download |
| `file` | `FileAsset` | `cms_file` | Generic file asset storage (documents, downloads, etc.) |
| `image` | `Image` | `cms_image` | Images with auto-generated thumbnails, width/height metadata |
| `logo` | `Logo` | `cms_logo` | Logos with automatic resize to 256px, SVG to PNG/JPG conversion |
| `question` | `Question` | `cms_question` | FAQ-style questions and answers |
| `video` | `Video` | `cms_video` | Videos, supports direct upload and async URL/YouTube download via yt-dlp |
| `website` | `Website` | `cms_website` | Website directory with URL, logo, and description |

---

## Shared Design Patterns

### BaseEntity

All CMS entities extend `BaseEntity`, which provides four audit columns managed by JPA lifecycle callbacks:

```
created_at  DATETIME  NOT NULL   — set on INSERT
updated_at  DATETIME  NOT NULL   — set on INSERT, updated on UPDATE
created_by  CHAR(36)  NULLABLE   — userId extracted from JWT on write
updated_by  CHAR(36)  NULLABLE   — userId extracted from JWT on write
```

### Internationalisation (i18n)

Every entity includes a `lang` column (`ENUM('EN','ZH')`, default `EN`). Content is stored per-language as separate rows rather than per-entity translations.

### Common Columns

All content entities share:

| Column | Type | Default | Purpose |
|---|---|---|---|
| `id` | `CHAR(36)` PK | UUID | Surrogate primary key |
| `lang` | `ENUM('EN','ZH')` | `EN` | Content language |
| `display_order` | `INT` | `0` | Client-side sort hint |
| `is_active` | `TINYINT(1)` | `1` | Soft-delete / visibility flag |
| `tags` | `VARCHAR(500)` | NULL | Comma-separated tags |
| `created_at` | `DATETIME` | now() | Audit — creation timestamp |
| `updated_at` | `DATETIME` | now() | Audit — last update timestamp |
| `created_by` | `CHAR(36)` | NULL | Audit — creator userId |
| `updated_by` | `CHAR(36)` | NULL | Audit — last updater userId |

### Soft Delete vs Hard Delete

Each entity supports two delete operations:

- `DELETE /{id}` — soft delete: sets `is_active = false`. Requires `ROLE_SUPER_ADMIN`.
- `DELETE /{id}/permanent` — hard delete: physically removes the DB row. Requires `ROLE_SUPER_ADMIN`.

### Dual Content-Type for Create/Update

Media endpoints (article, audio, image, logo, video) accept both:
- `multipart/form-data` — for direct file upload
- `application/json` — for URL-based creation (URL is fetched/downloaded server-side)

Non-media endpoints (question, website, app-settings) use `application/json` only.

---

## Authorization Model

| Role | Read | Create | Update | Soft Delete | Hard Delete |
|---|---|---|---|---|---|
| `ROLE_ADMIN` | ✓ | ✓ | ✓ | — | — |
| `ROLE_SUPER_ADMIN` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `ROLE_EDITOR` | — | ✓ (website only) | ✓ (website only) | — | — |
| Unauthenticated | — | — | — | — | — |

Media serving endpoints (`/view/{filename}`, `/cover/{filename}`) are public — no authentication required.

---

## Media Storage Strategy

All uploaded or downloaded files are stored on the **local filesystem**. Each module has its own upload directory configured via `UploadProperties` / `LogoUploadProperties`.

File paths are resolved using `CmsUtil.resolveSecurePath()` to prevent path traversal attacks.

```
<upload-base>/
├── articles/
│   ├── cover-images/
│   └── images/               ← article body images
├── audios/
│   └── cover-images/
├── files/
├── images/
├── logos/
│   └── deleted/              ← soft-deleted logos moved here
└── videos/
    └── cover-images/
```

---

## External Integrations

### yt-dlp (Video & Audio Download)

The `YtDlpService` wraps the `yt-dlp` command-line tool for downloading from YouTube and other supported platforms. It is used by:
- `VideoDownloadService` — downloads video + thumbnail
- `AudioDownloadService` — downloads audio-only (MP3 extraction)

`yt-dlp` must be installed on the host: `brew install yt-dlp`.

### Apache Batik (Logo SVG Conversion)

`LogoProcessingService` uses `org.apache.batik.transcoder.image.PNGTranscoder` to convert uploaded or URL-fetched SVG files to PNG (or PNG→JPG via intermediate step).

### Thumbnailator (Image/Logo Resize)

Both `LogoProcessingService` (logos resized to configurable `targetSize`, default 256px) and `ImageService` (thumbnail generation) use `net.coobird.thumbnailator.Thumbnails` for raster resize.

### Pinyin4j (Logo Filename Generation)

`LogoProcessingService` uses `net.sourceforge.pinyin4j.PinyinHelper` to convert Chinese character logo names into ASCII Pinyin for safe filesystem filenames.

---

## Dependency Graph

```
CMS Controllers
    │
    ├── ArticleController / AudioController / VideoController / ...
    │       └── [Entity]Service
    │               ├── [Entity]Repository  (Spring Data JPA)
    │               ├── VideoDownloadService (@Async)
    │               │       └── YtDlpService (yt-dlp shell exec)
    │               ├── AudioDownloadService (@Async)
    │               │       └── YtDlpService
    │               └── LogoProcessingService (Batik, Thumbnailator, Pinyin4j)
    │
    ├── JwtUtils  (extract userId from Authorization header)
    └── CmsUtil   (content-type detection, path resolution, sanitization)
```

---

## Key Configuration Properties

| Property prefix | Module | Description |
|---|---|---|
| `cms.upload.video.directory` | video | Filesystem path for video files |
| `cms.upload.video.download.max-resolution` | video | Max yt-dlp resolution (e.g., 1080) |
| `cms.upload.video.cover-image.max-size` | video | Max cover image dimension (px) |
| `cms.upload.audio.directory` | audio | Filesystem path for audio files |
| `cms.upload.logo.directory` | logo | Filesystem path for logo files |
| `cms.upload.logo.max-file-size` | logo | Max upload size in bytes |
| `cms.upload.logo.resize.target-size` | logo | Resize target dimension (default 256) |

---

*Last Updated: 2026-04-04*
