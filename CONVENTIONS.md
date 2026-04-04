# Conventions — GJP Admin API

> Used by Aider and other tools that look for CONVENTIONS.md.
> Auto-synced from AI.md — edit AI.md, then run `tooling/scripts/util/sync-ai-instructions.sh`

See AI.md for the full instructions.

## Quick Summary

- Spring Boot 3.x | Java 21 | MySQL 8 | JWT | Port 8083
- All responses: `ResponseEntity<ApiResponse<T>>`
- All endpoints need `@PreAuthorize`
- All entities extend `BaseEntity` — do not add audit fields manually
- Max page size: 100 — enforce `if (size > 100) size = 100;` in every search endpoint
- UUID PKs: `CHAR(36)` — never `AUTO_INCREMENT`
- Soft delete via `is_active` — use `/permanent` for hard delete
- Never store refresh tokens in plaintext — SHA-256 hash only
- System roles (`SUPER_ADMIN`, `ADMIN`, `USER`) are immutable — never modify or delete

## Context Files

- `tooling/docs/context/quick-reference.md` — entities, endpoints, patterns
- `tooling/docs/context/decisions.md` — architectural decisions (ADRs)
- `tooling/docs/context/todo.md` — planned work and known gaps
- `tooling/docs/guide/AI_CODING_GUIDE.md` — full coding rules
