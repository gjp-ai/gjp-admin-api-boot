# Quick Reference

One-page cheat sheet. Read this before writing any code for this project.

---

## Entities & Tables

### Auth Module (`org.ganjp.api.auth`)

| Entity | Table | Key Fields |
|---|---|---|
| `User` | `auth_users` | username, email, mobile, password_hash, status, role_ids |
| `Role` | `auth_roles` | name, code, parent_role_id, is_system |
| `UserRole` | `auth_user_roles` | user_id, role_id, expires_at |
| `RefreshToken` | `auth_refresh_tokens` | token_hash (SHA-256), user_id, expires_at, is_revoked |
| `TokenBlacklist` | `auth_token_blacklist` | jti, expires_at |
| `VerificationToken` | `auth_verification_tokens` | token_hash (SHA-256), user_id, type, expires_at, is_used |
| `UserSession` | `auth_user_sessions` | user_id, ip_address, last_active_at |

### CMS Module (`org.ganjp.api.cms`)

| Entity | Table | Notable Fields |
|---|---|---|
| `Article` | `cms_article` | title, content (longtext), cover_image_filename, lang, is_active |
| `ArticleImage` | `cms_article_image` | article_id, filename, width, height |
| `Audio` | `cms_audio` | filename, download_status, subtitle, artist |
| `FileAsset` | `cms_file` | filename, extension, mime_type |
| `Image` | `cms_image` | filename, thumbnail_filename, width, height, alt_text |
| `Logo` | `cms_logo` | filename (auto-generated), extension |
| `Question` | `cms_question` | question (255), answer (2000) |
| `Video` | `cms_video` | filename, download_status, cover_image_filename |
| `Website` | `cms_website` | name (128), url (500), logo_url |

### Master Module (`org.ganjp.api.master`)

| Entity | Table | Notable Fields |
|---|---|---|
| `AppSetting` | `master_app_settings` | name (50), value (500), lang, is_system, is_public |

### Common

| Entity | Table | Purpose |
|---|---|---|
| `AuditLog` | `audit_logs` | Async HTTP audit trail |

---

## All Endpoints

### Auth (`/v1/auth`)

```
POST   /v1/auth/register
POST   /v1/auth/login
POST   /v1/auth/refresh
POST   /v1/auth/logout
POST   /v1/auth/verify-email
POST   /v1/auth/resend-verification
POST   /v1/auth/forgot-password
POST   /v1/auth/reset-password
```

### Users (`/v1/users`)

```
GET    /v1/users                  ADMIN+
POST   /v1/users                  ADMIN+
GET    /v1/users/{id}             ADMIN+
PUT    /v1/users/{id}             ADMIN+
DELETE /v1/users/{id}             SUPER_ADMIN
GET    /v1/users/search           ADMIN+
GET    /v1/users/statistics       ADMIN+
PUT    /v1/users/{id}/password    ADMIN+
```

### Profile (self-service, `/v1/profile`)

```
GET    /v1/profile
PUT    /v1/profile
PUT    /v1/profile/password
```

### Roles (`/v1/roles`)

```
GET    /v1/roles                  ADMIN+
POST   /v1/roles                  SUPER_ADMIN
GET    /v1/roles/{id}             ADMIN+
PUT    /v1/roles/{id}             SUPER_ADMIN
DELETE /v1/roles/{id}             SUPER_ADMIN
GET    /v1/roles/hierarchy        ADMIN+
```

### Sessions (`/v1/sessions`)

```
GET    /v1/sessions               ADMIN+
DELETE /v1/sessions/{id}          ADMIN+
DELETE /v1/sessions/cleanup       SUPER_ADMIN
```

### CMS — Articles (`/v1/articles`)

```
GET    /v1/articles               ADMIN+   ?title&lang&tags&isActive&page&size&sort&direction
GET    /v1/articles/all           ADMIN+
POST   /v1/articles               ADMIN+   multipart/form-data OR application/json
GET    /v1/articles/{id}          ADMIN+
PUT    /v1/articles/{id}          ADMIN+   multipart/form-data OR application/json
DELETE /v1/articles/{id}          SUPER_ADMIN   (soft)
DELETE /v1/articles/{id}/permanent SUPER_ADMIN  (hard)
GET    /v1/articles/cover/{filename}  PUBLIC  (Range supported)
```

### CMS — Audio (`/v1/audios`)

```
GET    /v1/audios                 ADMIN+   ?name&lang&tags&isActive&page&size&sort&direction
GET    /v1/audios/all             ADMIN+
POST   /v1/audios                 ADMIN+   multipart = upload file; json = async URL/YouTube download
GET    /v1/audios/{id}            ADMIN+
PUT    /v1/audios/{id}            ADMIN+
DELETE /v1/audios/{id}            SUPER_ADMIN
DELETE /v1/audios/{id}/permanent  SUPER_ADMIN
GET    /v1/audios/view/{filename} PUBLIC   (Range supported)
GET    /v1/audios/cover/{filename} PUBLIC
```

### CMS — Files (`/v1/files`)

```
GET    /v1/files                  ADMIN+
POST   /v1/files                  ADMIN+   multipart/form-data
GET    /v1/files/{id}             ADMIN+
PUT    /v1/files/{id}             ADMIN+
DELETE /v1/files/{id}             SUPER_ADMIN
DELETE /v1/files/{id}/permanent   SUPER_ADMIN
GET    /v1/files/view/{filename}  PUBLIC
```

### CMS — Images (`/v1/images`)

```
GET    /v1/images                 ADMIN+   ?name&lang&tags&isActive&keyword&page&size&sort&direction
GET    /v1/images/all             ADMIN+
POST   /v1/images                 ADMIN+   multipart OR json (URL)
GET    /v1/images/{id}            ADMIN+
PUT    /v1/images/{id}            ADMIN+
DELETE /v1/images/{id}            SUPER_ADMIN
DELETE /v1/images/{id}/permanent  SUPER_ADMIN
GET    /v1/images/view/{filename} PUBLIC
```

### CMS — Logos (`/v1/logos`)

```
GET    /v1/logos                  ADMIN+   ?name&lang&tags&isActive&page&size&sort&direction
GET    /v1/logos/all              ADMIN+
POST   /v1/logos                  ADMIN+   multipart OR json (URL)
GET    /v1/logos/{id}             ADMIN+
PUT    /v1/logos/{id}             ADMIN+   json only
DELETE /v1/logos/{id}             SUPER_ADMIN
DELETE /v1/logos/{id}/permanent   SUPER_ADMIN
GET    /v1/logos/view/{filename}  PUBLIC
GET    /v1/logos/tag              ADMIN+   ?tag=xxx
```

### CMS — Questions (`/v1/questions`)

```
GET    /v1/questions              ADMIN+   ?question&lang&tags&isActive&page&size&sort&direction
GET    /v1/questions/{id}         ADMIN+
GET    /v1/questions/by-language/{lang}  ADMIN+  ?activeOnly
POST   /v1/questions              SUPER_ADMIN
PUT    /v1/questions/{id}         SUPER_ADMIN
DELETE /v1/questions/{id}         SUPER_ADMIN
DELETE /v1/questions/{id}/permanent  SUPER_ADMIN
```

### CMS — Videos (`/v1/videos`)

```
GET    /v1/videos                 ADMIN+   ?name&lang&tags&isActive&page&size&sort&direction
GET    /v1/videos/all             ADMIN+
POST   /v1/videos                 ADMIN+   multipart = upload; json = async URL/YouTube download
GET    /v1/videos/{id}            ADMIN+
PUT    /v1/videos/{id}            ADMIN+
DELETE /v1/videos/{id}            SUPER_ADMIN
DELETE /v1/videos/{id}/permanent  SUPER_ADMIN
GET    /v1/videos/view/{filename} PUBLIC   (Range supported)
GET    /v1/videos/cover/{filename} PUBLIC
```

### CMS — Websites (`/v1/websites`)

```
GET    /v1/websites               ADMIN+   ?name&lang&tags&isActive&page&size&sort&direction
GET    /v1/websites/{id}          ADMIN+
GET    /v1/websites/by-language/{lang}  ADMIN+  ?activeOnly
GET    /v1/websites/by-tag        ADMIN+   ?tag&activeOnly
GET    /v1/websites/top           ADMIN+   ?limit
GET    /v1/websites/statistics    ADMIN+
POST   /v1/websites               ADMIN+, EDITOR
PUT    /v1/websites/{id}          ADMIN+, EDITOR
DELETE /v1/websites/{id}          SUPER_ADMIN
DELETE /v1/websites/{id}/permanent  SUPER_ADMIN
PATCH  /v1/websites/{id}/activate   ADMIN+, EDITOR
PATCH  /v1/websites/{id}/deactivate ADMIN+, EDITOR
PATCH  /v1/websites/bulk/activate   ADMIN+
PATCH  /v1/websites/bulk/deactivate ADMIN+
```

### Master — App Settings (`/v1/app-settings`)

```
GET    /v1/app-settings           ADMIN+   ?searchTerm&lang&isPublic&isSystem&page&size&sort
GET    /v1/app-settings/{id}      ADMIN+
GET    /v1/app-settings/by-name   ADMIN+   ?name&lang
GET    /v1/app-settings/by-name/{name}  ADMIN+
GET    /v1/app-settings/by-language/{lang}  ADMIN+
GET    /v1/app-settings/public    PUBLIC
GET    /v1/app-settings/public/{lang}  PUBLIC
GET    /v1/app-settings/user-editable  ADMIN+
GET    /v1/app-settings/user-editable/{lang}  ADMIN+
GET    /v1/app-settings/value     ADMIN+   ?name&lang&defaultValue
GET    /v1/app-settings/names     ADMIN+
GET    /v1/app-settings/statistics  ADMIN+
GET    /v1/app-settings/exists    ADMIN+   ?name&lang
POST   /v1/app-settings           ADMIN+
PUT    /v1/app-settings/{id}      ADMIN+
PUT    /v1/app-settings/value     ADMIN+   ?name&lang&value
DELETE /v1/app-settings/{id}      ADMIN+
DELETE /v1/app-settings/by-name   ADMIN+   ?name&lang
```

---

## Mandatory Code Patterns

### Every endpoint must have

```java
@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
public ResponseEntity<ApiResponse<T>> methodName(...) {
    return ResponseEntity.ok(ApiResponse.success(data, "Message"));
}
```

### Response types

```java
ResponseEntity<ApiResponse<T>>           // single object
ResponseEntity<ApiResponse<PaginatedResponse<T>>>  // paginated list
ResponseEntity<ApiResponse<List<T>>>     // unpaginated list
ResponseEntity<ApiResponse<Void>>        // delete / action
```

### Pagination (max size enforced)

```java
if (size > 100) size = 100;
Pageable pageable = PageRequest.of(page, size, sortDirection, sort);
PaginatedResponse<T> response = PaginatedResponse.of(list.getContent(), list.getNumber(), list.getSize(), list.getTotalElements());
```

### Extract userId from JWT

```java
String userId = jwtUtils.extractUserIdFromToken(httpRequest);
```

### Async audit

```java
@Async("auditTaskExecutor")  // already wired globally — don't add manually
```

### Service transactions

```java
@Transactional           // normal operations
@Transactional(propagation = Propagation.REQUIRES_NEW)  // side-effects (email, audit)
```

### Entity base

```java
// Every entity extends BaseEntity — do NOT add createdAt/updatedAt/createdBy/updatedBy manually
public class MyEntity extends BaseEntity { ... }
```

---

## Key Constraints

| Rule | Value |
|---|---|
| Max page size | 100 (hard cap in every controller) |
| JWT access token TTL | 30 minutes |
| JWT refresh token TTL | 30 days |
| Login rate limit | 10 attempts / 60s per IP (in-memory) |
| BCrypt rounds | 2^10 |
| Password min length | 8 chars (upper + lower + digit + special) |
| UUID primary keys | `CHAR(36)`, set by app — never `AUTO_INCREMENT` |
| Soft delete column | `is_active TINYINT(1)` |
| Audit log retention | 300 days |
| System roles | `SUPER_ADMIN`, `ADMIN`, `USER` — immutable, never modify/delete |
| Refresh token storage | SHA-256 hash only — never plaintext |

---

## Package Structure

```
org.ganjp.api/
├── auth/          — JWT, users, roles, tokens, sessions, verification
├── cms/           — article, audio, file, image, logo, question, video, website
├── master/        — setting (app-wide config)
└── common/        — ApiResponse, BaseEntity, GlobalExceptionHandler, filters, utils
```

---

*Last Updated: 2026-04-04*
