# TODO & Known Gaps

Track planned work and known limitations. Update this file when items are completed or new ones are discovered.

---

## In Progress

*(nothing currently in progress)*

---

## Planned Work

### P1 — High Priority

- [ ] **Scheduled cleanup jobs** — These are designed but not yet implemented:
  - Refresh tokens older than 7 days → delete
  - Token blacklist entries past expiry → delete
  - Audit logs older than 300 days → delete
  - Unused verification tokens past expiry → delete

- [ ] **Tests** — No unit or integration tests exist yet. Coverage is 0%.
  - Auth module should be first priority (login, token refresh, RBAC)
  - Use `@SpringBootTest` + H2 for integration tests, `@ExtendWith(MockitoExtension.class)` for unit tests

### P2 — Medium Priority

- [ ] **Redis for rate limiting** — Current in-memory rate limiter (ADR-003) does not survive restarts and breaks in multi-instance deployments. Replace `LoginRateLimitFilter` with Redis-backed counter when scaling beyond one instance.

- [ ] **Redis for session tracking** — Current in-memory session store (ADR-004) is lost on restart. Replace with Redis when multi-instance or session persistence is required.

- [ ] **Database-backed active session tracking** — Alternative to Redis: persist sessions to a `auth_user_sessions` table so they survive restarts without requiring Redis.

- [ ] **FileAsset controller endpoints** — The `cms_file` table and `FileAsset` entity exist. Verify all CRUD and file-serving endpoints are implemented and documented.

### P3 — Low Priority

- [ ] **CMS article images API** — `cms_article_image` table and `ArticleImage` entity exist but there may be gaps in the API for managing body images attached to articles. Review and complete if needed.

- [ ] **Postman collection** — Keep the Postman collection in sync as new endpoints are added.

- [ ] **SQL schema scripts** — Ensure `tooling/scripts/database/mysql/` reflects the current schema including all CMS and Master tables.

---

## Known Limitations

| Limitation | Details | ADR |
|---|---|---|
| Rate limiting is single-instance only | In-memory `ConcurrentHashMap`, resets on restart | ADR-003 |
| Session tracking is single-instance only | In-memory map, lost on restart | ADR-004 |
| yt-dlp must be installed on host | `brew install yt-dlp` required for video/audio URL downloads | ADR-011 |
| No multi-language support beyond EN/ZH | `lang ENUM('EN','ZH')` is hardcoded; adding a language requires a schema migration | ADR-008 |
| Logo resize target size is global | All logos resize to the same `targetSize` (default 256px); per-logo size is not supported | — |
| Audit logs have no DB FK to users | Intentional — see ADR-005 | ADR-005 |

---

## Decided Against (Do Not Re-propose)

| Idea | Reason |
|---|---|
| Redis (currently) | Not needed until multi-instance deployment — adds ops complexity for no current benefit |
| `@EnableJpaAuditing` / `AuditorAware` | Breaks in async contexts — see ADR-009 |
| DB FK on `audit_logs.user_id` | Must survive user deletion — see ADR-005 |
| DB FK on `cms_article_image.article_id` | Images should survive article soft-delete — see ADR-010 |
| Layer-based package structure | Feature-based is deliberate — see ADR-007 |
| Single long-lived JWT | Cannot be revoked without full blacklist — see ADR-001 |

---

*Last Updated: 2026-04-04*
