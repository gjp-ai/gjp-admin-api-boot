# Architecture Decisions

Explains *why* key choices were made. Read before suggesting changes to these areas.

---

## ADR-001 — Dual-Token JWT Strategy (access + refresh)

**Decision:** Use a short-lived access token (30 min) and a long-lived refresh token (30 days).

**Why:** A single long-lived token cannot be revoked without a blacklist covering its full lifetime. With dual tokens, revoking a refresh token immediately cuts off new access token issuance. The access token TTL is short enough that a stolen token expires quickly without requiring per-request DB lookups.

**Consequences:** Clients must implement token refresh logic. Refresh tokens are rotated on each use (old revoked, new issued) to detect theft.

---

## ADR-002 — Refresh Tokens Stored as SHA-256 Hash

**Decision:** Only the SHA-256 hash of the refresh token is stored in `auth_refresh_tokens`. The raw token is returned to the client once and never stored.

**Why:** If the database is compromised, attackers cannot use the stored hashes to authenticate. The client holds the only copy of the raw token.

**Consequences:** Cannot look up a refresh token by anything other than its hash. Token recovery is impossible — if lost, the user must log in again.

---

## ADR-003 — In-Memory Rate Limiting (not Redis)

**Decision:** Login rate limiting (10 attempts / 60s per IP) is implemented in-memory using a `ConcurrentHashMap` in `LoginRateLimitFilter`.

**Why:** Redis adds operational complexity (another service to run and monitor). For a single-instance deployment this is sufficient.

**Consequence:** Rate limit state is lost on restart, and does not work correctly in a multi-instance deployment. **Do not change this to Redis without first confirming a multi-instance requirement.**

---

## ADR-004 — In-Memory Session Tracking

**Decision:** Active user sessions are tracked in-memory.

**Why:** Same reasoning as ADR-003. Avoids Redis dependency for a single-instance deployment.

**Consequence:** Sessions are cleared on restart. Sessions are not shared across multiple instances.

---

## ADR-005 — No Foreign Key on `audit_logs.user_id`

**Decision:** The `audit_logs` table does not have a database-level FK to `auth_users.id`.

**Why:** Audit logs are append-only and must survive user deletion. A FK would either prevent user deletion or cascade-delete audit history, both of which are unacceptable.

**Consequences:** Referential integrity on `user_id` is not enforced at the DB level. This is intentional.

---

## ADR-006 — Soft Delete via `is_active`

**Decision:** All entities use `is_active TINYINT(1)` for soft delete. A separate `DELETE /{id}/permanent` endpoint performs hard delete.

**Why:** Prevents accidental data loss. Preserves audit history. Allows recovery.

**Consequence:** Queries that should only return active records must explicitly filter by `is_active = true`. The `/permanent` endpoint exists for when physical removal is genuinely needed.

---

## ADR-007 — Feature-Based Package Structure (not layer-based)

**Decision:** Code is organised by feature (`auth/user/`, `cms/video/`) rather than by layer (`controller/`, `service/`, `repository/`).

**Why:** Feature-based structure means all code related to a feature is co-located. Adding or removing a feature is a single directory operation. Layer-based structure scatters a feature across the entire codebase.

**Consequence:** Each feature package contains its own controller, service, repository, entity, and DTOs. Common infrastructure lives in `common/`.

---

## ADR-008 — Per-Language Rows for i18n

**Decision:** Multilingual content is stored as separate rows with a `lang ENUM('EN','ZH')` column, not as JSON blobs or nullable translation columns.

**Why:** Simple queries, proper indexing, and no nullable-column explosion as languages are added. Each row is a complete, self-contained record in one language.

**Consequence:** To provide content in both languages, insert two rows with the same logical key but different `lang` values. The `(name, lang)` pair is the effective unique key for settings; content entities use separate UUIDs per language.

---

## ADR-009 — No JPA @CreatedBy / @LastModifiedBy Auditing

**Decision:** `BaseEntity` sets `createdBy` / `updatedBy` manually from the JWT-extracted `userId`, not via Spring Data's `@EnableJpaAuditing` / `AuditorAware`.

**Why:** `AuditorAware` pulls the auditor from Spring Security's `SecurityContext`. In async contexts (e.g., `VideoDownloadService`, `AuditLogService`) the `SecurityContext` is not propagated to the background thread, which would result in null auditor values. Manual assignment is explicit and reliable.

**Consequence:** Controllers must call `jwtUtils.extractUserIdFromToken(request)` and pass `userId` down to the service layer on every write operation.

---

## ADR-010 — App-Enforced Referential Integrity for `cms_article_image`

**Decision:** `cms_article_image.article_id` does not have a database-level FK to `cms_article.id`.

**Why:** Consistent with the audit log pattern (ADR-005). Article images should survive article soft-delete for potential reuse or audit. A DB FK with `ON DELETE CASCADE` would silently destroy image records on article deletion.

**Consequence:** Orphaned image records are possible if an article is hard-deleted. The application layer is responsible for cleanup.

---

## ADR-011 — yt-dlp for Video/Audio Download

**Decision:** Video and audio URL downloads use the external `yt-dlp` CLI tool rather than a Java library.

**Why:** No mature Java library supports the breadth of platforms yt-dlp handles (YouTube, Vimeo, etc.) with active maintenance. yt-dlp is the de facto standard and is actively maintained.

**Consequence:** `yt-dlp` must be installed on the host machine (`brew install yt-dlp`). The application checks for its presence at runtime and returns a clear error if missing.

---

## ADR-012 — Apache Batik for SVG-to-PNG Conversion

**Decision:** Logo SVG files are converted to PNG (or JPG) using Apache Batik's `PNGTranscoder`.

**Why:** Batik is the most mature Java SVG rendering library and handles complex SVG features reliably. Pure-Java alternatives have incomplete SVG spec support.

**Consequence:** Batik adds ~10 MB to the JAR. SVG-to-JPG requires an intermediate SVG→PNG step since Batik only provides a `PNGTranscoder`.

---

## ADR-013 — System Role Protection

**Decision:** Roles with `is_system = true` (`SUPER_ADMIN`, `ADMIN`, `USER`) cannot be modified or deleted via the API.

**Why:** These roles are referenced in `@PreAuthorize` annotations throughout the codebase. Deleting or renaming them would silently break all authorization checks.

**Consequence:** Only non-system roles can be created/updated/deleted by users. System roles are seeded by DB migration scripts.

---

*Last Updated: 2026-04-04*
