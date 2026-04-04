# CMS Module — API Reference

## Base URL

```
http://localhost:8083/api
```

All CMS endpoints use the path prefix `/v1/`.

## Response Envelope

All endpoints (except media-serving) return:

```json
{
  "status": 200,
  "message": "...",
  "data": { ... },
  "meta": { "requestId": "uuid" }
}
```

Paginated list endpoints return `data` as:

```json
{
  "content": [ ... ],
  "page": 0,
  "size": 20,
  "totalElements": 100
}
```

## Common Query Parameters (Search Endpoints)

| Parameter | Type | Default | Description |
|---|---|---|---|
| `page` | int | `0` | Page number (0-based) |
| `size` | int | `20` | Page size (max 100, capped server-side) |
| `sort` | string | `updatedAt` | Sort field |
| `direction` | string | `desc` | Sort direction: `asc` or `desc` |
| `lang` | enum | — | Filter by language: `EN` or `ZH` |
| `tags` | string | — | Filter by tag value (contains match) |
| `isActive` | boolean | — | Filter by active status |

## Authentication

All endpoints except media-serving require a valid JWT in the `Authorization: Bearer <token>` header.

---

## Articles

### `GET /v1/articles`

Search articles with pagination.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `page`, `size`, `sort`, `direction`, `title` (contains), `lang`, `tags`, `isActive`

**Response 200:**
```json
{
  "status": 200,
  "message": "Articles found",
  "data": {
    "content": [
      {
        "id": "uuid",
        "title": "Article Title",
        "summary": "Short summary",
        "originalUrl": "https://...",
        "sourceName": "Source Name",
        "coverImageFilename": "cover.jpg",
        "tags": "tag1,tag2",
        "lang": "EN",
        "displayOrder": 0,
        "isActive": true,
        "createdAt": "2026-04-04T10:00:00",
        "updatedAt": "2026-04-04T10:00:00"
      }
    ],
    "page": 0,
    "size": 20,
    "totalElements": 1
  }
}
```

---

### `GET /v1/articles/all`

Return all articles (no pagination).

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `POST /v1/articles` — `multipart/form-data`

Create an article with optional cover image file upload.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Form fields:** `title` (required), `summary`, `content`, `originalUrl`, `sourceName`, `tags`, `lang`, `displayOrder`, `isActive`, `coverImage` (file)

**Response 201**

---

### `POST /v1/articles` — `application/json`

Create an article using a cover image URL.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Request body:**
```json
{
  "title": "Article Title",
  "summary": "Short summary",
  "content": "<p>Full content</p>",
  "originalUrl": "https://source.com/article",
  "sourceName": "Source Name",
  "coverImageOriginalUrl": "https://source.com/image.jpg",
  "tags": "tag1,tag2",
  "lang": "EN",
  "displayOrder": 0,
  "isActive": true
}
```

**Response 201**

---

### `GET /v1/articles/{id}`

Get a single article by ID.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Response 200** — article object

---

### `PUT /v1/articles/{id}` — `multipart/form-data` or `application/json`

Update an article. Same fields as create.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Response 200** — updated article

---

### `DELETE /v1/articles/{id}`

Soft delete (sets `isActive = false`).

**Auth:** `ROLE_SUPER_ADMIN`

**Response 200:** `{ "data": null, "message": "Article deleted" }`

---

### `DELETE /v1/articles/{id}/permanent`

Hard delete (removes DB row and associated files).

**Auth:** `ROLE_SUPER_ADMIN`

**Response 200**

---

### `GET /v1/articles/cover/{filename}`

Serve article cover image. Supports HTTP Range requests (206 Partial Content).

**Auth:** None

**Response:** Binary image stream with content-type detected from filename extension.

---

## Audio

### `GET /v1/audios`

Search audios with pagination.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `page`, `size`, `sort`, `direction`, `name` (contains), `lang`, `tags`, `isActive`

---

### `GET /v1/audios/all`

Return all audios (no pagination).

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `POST /v1/audios` — `multipart/form-data`

Upload an audio file directly.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Form fields:** `name` (required), `file` (audio file), `coverImage` (image file), `description`, `subtitle`, `artist`, `tags`, `lang`, `displayOrder`, `isActive`

**Response 201**

---

### `POST /v1/audios` — `application/json`

Start async download of audio from a URL or YouTube (audio-only extraction).

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Request body:**
```json
{
  "name": "My Audio",
  "originalUrl": "https://www.youtube.com/watch?v=...",
  "filename": "my-audio.mp3",
  "description": "Description",
  "tags": "music,english",
  "lang": "EN"
}
```

**Response 201:** Record created with `downloadStatus: "PENDING"`. Poll `GET /v1/audios/{id}` to check progress.

---

### `GET /v1/audios/{id}`

Get a single audio record. Use to poll `downloadStatus` after async download.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Response 200** — includes `downloadStatus` (`PENDING` | `DOWNLOADING` | `COMPLETED` | `FAILED`) and `downloadError` if failed.

---

### `PUT /v1/audios/{id}` — `multipart/form-data` or `application/json`

Update audio metadata.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/audios/{id}`

Soft delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/audios/{id}/permanent`

Hard delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `GET /v1/audios/view/{filename}`

Stream audio file. Supports HTTP Range requests (206 Partial Content) — required for seek support in HTML5 `<audio>`.

**Auth:** None

---

### `GET /v1/audios/cover/{filename}`

Serve audio cover image.

**Auth:** None

---

## Files

### `GET /v1/files`

Search file assets with pagination.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `page`, `size`, `sort`, `direction`, `name` (contains), `lang`, `tags`, `isActive`

---

### `POST /v1/files` — `multipart/form-data`

Upload a generic file.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Form fields:** `name`, `file` (required), `tags`, `lang`, `displayOrder`, `isActive`

**Response 201**

---

### `GET /v1/files/{id}`

Get a file record by ID.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `PUT /v1/files/{id}`

Update file metadata.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/files/{id}`

Soft delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/files/{id}/permanent`

Hard delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `GET /v1/files/view/{filename}`

Serve the file for download or inline viewing.

**Auth:** None

---

## Images

### `GET /v1/images`

Search images with pagination.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `page`, `size`, `sort`, `direction`, `name`, `lang`, `tags`, `isActive`, `keyword` (legacy contains-search, overrides `name` if provided)

---

### `GET /v1/images/all`

Return all images (no pagination).

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `POST /v1/images` — `multipart/form-data`

Upload an image. A thumbnail is auto-generated on upload.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Form fields:** `name` (required), `file` (required), `altText`, `tags`, `lang`, `displayOrder`, `isActive`

**Response 201** — includes `filename`, `thumbnailFilename`, `width`, `height`, `mimeType`, `sizeBytes`

---

### `POST /v1/images` — `application/json`

Download and store an image from a URL.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Request body:**
```json
{
  "name": "Image Name",
  "originalUrl": "https://example.com/image.jpg",
  "altText": "Alt text",
  "tags": "nature,landscape",
  "lang": "EN"
}
```

**Response 201**

---

### `GET /v1/images/{id}`

Get image by ID.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `PUT /v1/images/{id}` — `application/json`

Update image metadata (name, altText, tags, lang, displayOrder, isActive).

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/images/{id}`

Soft delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/images/{id}/permanent`

Hard delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `GET /v1/images/view/{filename}`

Serve image inline.

**Auth:** None

**Response:** Binary image stream, content-type from extension.

---

## Logos

### `GET /v1/logos`

Search logos with pagination.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `page`, `size`, `sort`, `direction`, `name`, `lang`, `tags`, `isActive`

---

### `GET /v1/logos/all`

Return all logos.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `POST /v1/logos` — `multipart/form-data`

Upload a logo file. Raster images are resized to 256px (longest side). SVGs are stored as-is.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Form fields:** `name` (required), `file` (required), `tags`, `lang`, `displayOrder`, `isActive`

**Response 201** — filename is auto-generated as `{name_snake_case}_{size}_{timestamp}.{ext}`

---

### `POST /v1/logos` — `application/json`

Fetch and store a logo from a URL. Same processing as file upload.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Request body:**
```json
{
  "name": "Company Logo",
  "originalUrl": "https://company.com/logo.svg",
  "tags": "brand,official",
  "lang": "EN"
}
```

**Response 201**

---

### `GET /v1/logos/{id}`

Get logo by ID.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `PUT /v1/logos/{id}` — `application/json`

Update logo metadata.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/logos/{id}`

Soft delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/logos/{id}/permanent`

Hard delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `GET /v1/logos/view/{filename}`

Serve logo file inline (supports SVG, PNG, JPG, etc.).

**Auth:** None

---

### `GET /v1/logos/tag?tag={value}`

Find all logos containing the specified tag.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Response 200:** Array of logo objects.

---

## Questions

### `GET /v1/questions`

Search questions with pagination.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `page`, `size`, `sort`, `direction`, `question` (contains), `lang`, `tags`, `isActive`

---

### `GET /v1/questions/{id}`

Get a question by ID.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `GET /v1/questions/by-language/{lang}`

Get all questions in the specified language.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Path variable:** `lang` — `EN` or `ZH`

**Query param:** `activeOnly` (boolean, default `false`)

---

### `POST /v1/questions`

Create a question.

**Auth:** `ROLE_SUPER_ADMIN`

**Request body:**
```json
{
  "question": "What is GJP?",
  "answer": "GJP is...",
  "tags": "faq,general",
  "lang": "EN",
  "displayOrder": 0,
  "isActive": true
}
```

**Response 201**

---

### `PUT /v1/questions/{id}`

Update a question.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/questions/{id}`

Soft delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/questions/{id}/permanent`

Hard delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

## Videos

### `GET /v1/videos`

Search videos with pagination.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `page`, `size`, `sort`, `direction`, `name` (contains), `lang`, `tags`, `isActive`

---

### `GET /v1/videos/all`

Return all videos (no pagination).

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `POST /v1/videos` — `multipart/form-data`

Upload a video file directly.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Form fields:** `name` (required), `file` (video file), `coverImage` (image file), `description`, `tags`, `lang`, `displayOrder`, `isActive`

**Response 201**

---

### `POST /v1/videos` — `application/json`

Start async download of a video from a URL or YouTube.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Request body:**
```json
{
  "name": "Tutorial Video",
  "originalUrl": "https://www.youtube.com/watch?v=...",
  "filename": "tutorial.mp4",
  "coverImageFilename": "tutorial-cover.jpg",
  "coverImageUrl": "https://...",
  "description": "Video description",
  "tags": "tutorial,coding",
  "lang": "EN"
}
```

**Response 201:** Record created with `downloadStatus: "PENDING"`. Poll `GET /v1/videos/{id}` to check progress (`PENDING` → `DOWNLOADING` → `COMPLETED` / `FAILED`).

---

### `GET /v1/videos/{id}`

Get a video record. Use to poll download status.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `PUT /v1/videos/{id}` — `multipart/form-data` or `application/json`

Update video metadata.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/videos/{id}`

Soft delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/videos/{id}/permanent`

Hard delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `GET /v1/videos/view/{filename}`

Stream a video file. Supports HTTP Range requests (206 Partial Content) — required for HTML5 `<video>` seek.

**Auth:** None

---

### `GET /v1/videos/cover/{filename}`

Serve video cover/thumbnail image.

**Auth:** None

---

## Websites

### `GET /v1/websites`

Search websites with pagination.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `page`, `size`, `sort`, `direction`, `name`, `lang`, `tags`, `isActive`

---

### `GET /v1/websites/{id}`

Get a website by ID.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `GET /v1/websites/by-language/{lang}`

Get all websites in a specific language.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query param:** `activeOnly` (boolean, default `false`)

---

### `GET /v1/websites/by-tag?tag={value}&activeOnly={bool}`

Find websites by tag.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `tag` (required), `activeOnly` (default `true`)

---

### `GET /v1/websites/top?limit={n}`

Get top N active websites ordered by `display_order`.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query param:** `limit` (default `10`)

---

### `GET /v1/websites/statistics`

Get website statistics (total count, active count, counts by language, etc.).

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

---

### `POST /v1/websites`

Create a website entry.

**Auth:** `ROLE_SUPER_ADMIN`, `ROLE_ADMIN`, or `ROLE_EDITOR`

**Request body:**
```json
{
  "name": "Example Site",
  "url": "https://example.com",
  "logoUrl": "https://example.com/logo.png",
  "description": "A great website",
  "tags": "tech,resource",
  "lang": "EN",
  "displayOrder": 0,
  "isActive": true
}
```

**Response 201**

---

### `PUT /v1/websites/{id}`

Update a website.

**Auth:** `ROLE_SUPER_ADMIN`, `ROLE_ADMIN`, or `ROLE_EDITOR`

---

### `DELETE /v1/websites/{id}`

Soft delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `DELETE /v1/websites/{id}/permanent`

Hard delete.

**Auth:** `ROLE_SUPER_ADMIN`

---

### `PATCH /v1/websites/{id}/deactivate`

Set `isActive = false`.

**Auth:** `ROLE_SUPER_ADMIN`, `ROLE_ADMIN`, or `ROLE_EDITOR`

---

### `PATCH /v1/websites/{id}/activate`

Set `isActive = true`.

**Auth:** `ROLE_SUPER_ADMIN`, `ROLE_ADMIN`, or `ROLE_EDITOR`

---

### `PATCH /v1/websites/bulk/activate`

Bulk activate websites.

**Auth:** `ROLE_SUPER_ADMIN` or `ROLE_ADMIN`

**Request body:** `["id1", "id2", ...]`

**Response 200:** `"Successfully activated N websites"`

---

### `PATCH /v1/websites/bulk/deactivate`

Bulk deactivate websites.

**Auth:** `ROLE_SUPER_ADMIN` or `ROLE_ADMIN`

**Request body:** `["id1", "id2", ...]`

---

## Authorization Matrix Summary

| Endpoint group | Read | Create | Update | Soft Delete | Hard Delete |
|---|---|---|---|---|---|
| Articles | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN | SUPER_ADMIN | SUPER_ADMIN |
| Audios | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN | SUPER_ADMIN | SUPER_ADMIN |
| Files | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN | SUPER_ADMIN | SUPER_ADMIN |
| Images | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN | SUPER_ADMIN | SUPER_ADMIN |
| Logos | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN | SUPER_ADMIN | SUPER_ADMIN |
| Questions | ADMIN, SUPER_ADMIN | **SUPER_ADMIN only** | **SUPER_ADMIN only** | SUPER_ADMIN | SUPER_ADMIN |
| Videos | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN | SUPER_ADMIN | SUPER_ADMIN |
| Websites | ADMIN, SUPER_ADMIN | ADMIN, SUPER_ADMIN, **EDITOR** | ADMIN, SUPER_ADMIN, **EDITOR** | SUPER_ADMIN | SUPER_ADMIN |

Media serving endpoints (`/view/{filename}`, `/cover/{filename}`) are **public** (no auth required).

---

## Error Codes

| HTTP Status | Condition |
|---|---|
| `400 Bad Request` | Validation failure, filename collision, unsupported format |
| `401 Unauthorized` | Missing or expired JWT |
| `403 Forbidden` | Insufficient role |
| `404 Not Found` | Entity ID not found |
| `409 Conflict` | Filename already exists |
| `500 Internal Server Error` | Unexpected error (e.g., yt-dlp not installed) |

---

*Last Updated: 2026-04-04*
