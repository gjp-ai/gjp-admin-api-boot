# CMS Module — Media Processing

## Overview

The CMS module handles several types of media processing:

| Processing Type | Applies To | Library / Tool |
|---|---|---|
| Async video download | Video (URL/YouTube) | `VideoDownloadService` + `YtDlpService` |
| Async audio download | Audio (URL/YouTube) | `AudioDownloadService` + `YtDlpService` |
| Logo resize to 256px | Logo (upload & URL) | Thumbnailator |
| SVG → PNG/JPG conversion | Logo | Apache Batik |
| WebP → PNG/JPG conversion | Logo (upload & URL) | TwelveMonkeys ImageIO + Thumbnailator |
| Thumbnail generation | Image | Thumbnailator |
| Cover image download/resize | Video, Audio | Standard Java HTTP + `javax.imageio` |
| Filename generation (Chinese) | Logo | Pinyin4j |
| Path security | All | `CmsUtil.resolveSecurePath()` |

---

## Video Download Flow

### Direct Upload

`POST /v1/videos` with `multipart/form-data`:

1. File is received as `MultipartFile` in `VideoCreateRequest`
2. Saved to `<video-dir>/{filename}`
3. `Video` record created with `downloadStatus = null`, `filename` set immediately
4. Returns 201 synchronously

### URL / YouTube Download (Async)

`POST /v1/videos` with `application/json`:

```
Client → POST /v1/videos (JSON)
             │
             ▼
       VideoService.createVideoByUrl()
             │  — creates Video record with downloadStatus=PENDING
             │  — saves to DB
             │  — calls VideoDownloadService.downloadInBackground() (@Async)
             │
             ▼ (returns 201 immediately)
       Client polls GET /v1/videos/{id}

Background thread (VideoDownloadService):
       downloadInBackground()
             │  — sets downloadStatus=DOWNLOADING
             │
             ├── isYouTubeUrl?
             │       YES → YtDlpService.download()
             │               — executes: yt-dlp -f "bestvideo[height<=1080]+bestaudio" -o <path> <url>
             │               — returns: filename, filePath, metadata (thumbnailUrl)
             │               — downloads thumbnail → resizes → saves as cover image
             │
             │       NO  → HTTP download via java.net.URL.openStream()
             │               — streams bytes to <video-dir>/{filename}
             │               — downloads coverImageUrl if provided
             │
             ▼
       sets downloadStatus=COMPLETED (or FAILED + downloadError)
       saves to DB
```

### Download Status Lifecycle

```
null          → direct file upload (no async download)
PENDING       → record created, download not yet started
DOWNLOADING   → background thread active
COMPLETED     → file on disk, filename set in DB
FAILED        → error stored in downloadError (max 500 chars)
```

### YtDlpService

`YtDlpService` wraps the `yt-dlp` CLI:

- Checks availability via `yt-dlp --version` on startup
- Download command: `yt-dlp -f "bestvideo[height<={maxResolution}]+bestaudio/best[height<={maxResolution}]" --merge-output-format mp4 -o <output-path> <url>`
- Returns `DownloadResult` containing:
  - `filename` — actual filename written to disk
  - `filePath` — full path
  - `metadata.thumbnailUrl` — best thumbnail URL for cover image download
- `maxResolution` is configurable via `cms.upload.video.download.max-resolution` (default 1080)

**Requirement:** `yt-dlp` must be installed on the host:
```bash
brew install yt-dlp       # macOS
pip install yt-dlp        # any platform
```

---

## Audio Download Flow

Identical pattern to video download, managed by `AudioDownloadService`:

- For YouTube URLs: yt-dlp extracts **audio only** as MP3 (`-x --audio-format mp3`)
- For direct URLs: HTTP stream download
- Cover image handling identical to video

```
PENDING → DOWNLOADING → COMPLETED / FAILED
```

---

## Logo Processing Flow

### Upload Flow

```
POST /v1/logos (multipart/form-data)
      │
      ▼
LogoProcessingService.processUploadedFile(file, logoName)
      │
      ├── Validate: not empty, size < max, content-type is image/*
      │
      ├── Is SVG?
      │       YES → saveSvgFile() — store as-is, no resize
      │
      └── Is raster (JPG/PNG/GIF/BMP)?
              │
              ├── Is WebP?
              │       YES → convert to PNG or JPG via TwelveMonkeys ImageIO
              │
              └── resizeAndSave()
                      — calculate new dimensions to fit in targetSize px (default 256)
                      — Thumbnails.of(image).size(w, h).outputFormat(ext).toFile(path)
```

### URL Fetch Flow

```
POST /v1/logos (application/json)
      │
      ▼
LogoProcessingService.processImageFromUrl(url, logoName)
      │
      ├── Extract extension from URL
      │
      ├── Is SVG?
      │       YES → stream from URL → saveSvgFile()
      │
      └── Is raster?
              — ImageIO.read(url.toURL())
              — WebP conversion if needed
              — resizeAndSave()
```

### SVG → PNG Conversion

Triggered by `LogoProcessingService.convertImageFormat(sourceFile, "png", logoName)`:

```java
PNGTranscoder transcoder = new PNGTranscoder();   // Apache Batik
transcoder.transcode(
    new TranscoderInput(svgInputStream),
    new TranscoderOutput(pngOutputStream)
);
```

SVG → JPG requires an intermediate SVG → PNG step, then `ImageIO.write(bufferedImage, "jpg", file)`.

### Filename Generation

Logo filenames are generated deterministically from the logo name:

```
Input:  "My Company Logo 2024"
Output: "my_company_logo_2024_256_20260404120000.png"

Input:  "我的标志"
Output: "wo_de_biao_zhi_256_20260404120000.png"
```

Rules:
1. Chinese characters → Pinyin (via Pinyin4j), inserted with `_` separators
2. CamelCase → `_` before uppercase: `myLogo` → `my_logo`
3. Spaces, `-`, `_` → single `_`
4. Special characters stripped
5. Append `_{targetSize}_{yyyyMMddHHmmss}.{ext}`

---

## Image Processing (cms_image)

On upload, `ImageService` uses Thumbnailator to generate a thumbnail:

```
Original image → stored as {uuid}.{ext}
               → thumbnail generated → stored as {uuid}_thumb.{ext}
               → width, height, mimeType, sizeBytes extracted and saved to DB
```

WebP images are handled via TwelveMonkeys ImageIO plugin, which allows `javax.imageio.ImageIO` to read WebP.

---

## HTTP Range Requests (Media Streaming)

Video and audio serve endpoints support `Range` request headers to enable:
- Seeking in HTML5 `<video>` and `<audio>` elements
- Resumable downloads

Implementation in `VideoController.viewVideo()` and `AudioController.viewAudio()`:

```
Request:  GET /v1/videos/view/tutorial.mp4
          Range: bytes=0-1048575

Response: HTTP 206 Partial Content
          Content-Range: bytes 0-1048575/52428800
          Accept-Ranges: bytes
          Content-Length: 1048576
          Content-Type: video/mp4
          [binary data]
```

If no `Range` header is present, the full file is streamed with HTTP 200.

Implementation uses `java.io.RandomAccessFile.seek(start)` to efficiently read only the requested byte range without loading the full file into memory.

---

## Path Security

All file paths are resolved through `CmsUtil.resolveSecurePath(baseDir, filename)` to prevent path traversal attacks:

- Resolves the full path within the base directory
- Throws `IllegalArgumentException` if the resolved path escapes the base directory

Filenames passed to serve endpoints (`/view/{filename}`, `/cover/{filename}`) are sanitized via `CmsUtil.sanitizeFilename()` before use in `Content-Disposition` headers.

---

## File Storage Layout

```
<cms-upload-base>/
├── articles/
│   ├── my-cover.jpg           ← article cover images
│   └── images/
│       └── body-image.png     ← article body images
├── audios/
│   ├── my-audio.mp3
│   └── cover-images/
│       └── my-cover.jpg
├── files/
│   └── document.pdf
├── images/
│   ├── photo.jpg
│   └── photo_thumb.jpg
├── logos/
│   ├── my_logo_256_20260404.png
│   └── deleted/               ← soft-deleted logos moved here
└── videos/
    ├── tutorial.mp4
    └── cover-images/
        └── tutorial-cover.jpg
```

---

## Content-Type Detection

`CmsUtil.determineContentType(filename)` maps file extensions to MIME types for HTTP responses. Common mappings:

| Extension | MIME Type |
|---|---|
| `.jpg`, `.jpeg` | `image/jpeg` |
| `.png` | `image/png` |
| `.gif` | `image/gif` |
| `.webp` | `image/webp` |
| `.svg` | `image/svg+xml` |
| `.mp4` | `video/mp4` |
| `.webm` | `video/webm` |
| `.mp3` | `audio/mpeg` |
| `.wav` | `audio/wav` |
| `.pdf` | `application/pdf` |

---

*Last Updated: 2026-04-04*
