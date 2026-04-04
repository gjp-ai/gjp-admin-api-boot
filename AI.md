# GJP Admin API — AI Instructions

This file is the universal source of truth for AI assistants working in this repository.
Tool-specific files (CLAUDE.md, .cursorrules, .github/copilot-instructions.md, etc.) are
kept in sync with this file via `tooling/scripts/util/sync-ai-instructions.sh`.

---

## Project Overview

GJP Admin API — a Spring Boot 3.x REST API with JWT authentication, role-based authorization,
async audit logging, and modular architecture.

- **Port:** 8082 | **Context path:** `/api/` | **Base URL:** `http://localhost:8082/api`
- **Java 21** | **Spring Boot 3.4.5** | **MySQL 8** | **JWT (JJWT 0.11.5)**

---

## Read These Before Starting Any Task

| File | What it contains |
|---|---|
| `tooling/docs/context/quick-reference.md` | All entities, tables, endpoints, and mandatory patterns |
| `tooling/docs/context/decisions.md` | Why key architectural decisions were made — do not re-propose items listed here |
| `tooling/docs/context/todo.md` | In-progress work, known gaps, and items decided against |
| `tooling/docs/guide/AI_CODING_GUIDE.md` | Coding rules — follow before writing any code |

---

## Mandatory Code Patterns

### Every response
```java
ResponseEntity<ApiResponse<T>>                      // single object
ResponseEntity<ApiResponse<PaginatedResponse<T>>>   // paginated list
ResponseEntity<ApiResponse<Void>>                   // delete / action
```

### Every endpoint
```java
@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
public ResponseEntity<ApiResponse<T>> method(...) {
    return ResponseEntity.ok(ApiResponse.success(data, "Message"));
}
```

### Pagination (max size enforced server-side)
```java
if (size > 100) size = 100;
Pageable pageable = PageRequest.of(page, size, sortDirection, sort);
```

### Extract userId from JWT
```java
String userId = jwtUtils.extractUserIdFromToken(httpRequest);
```

### Entity base class
```java
// DO NOT add createdAt/updatedAt/createdBy/updatedBy — BaseEntity provides them
public class MyEntity extends BaseEntity { ... }
```

---

## Package Structure

```
org.ganjp.api/
├── auth/     — JWT, users, roles, tokens, sessions, verification
├── cms/      — article, audio, file, image, logo, question, video, website
├── master/   — setting (app-wide config with i18n)
└── common/   — ApiResponse, BaseEntity, GlobalExceptionHandler, filters, utils
```

---

## Key Constraints

| Rule | Value |
|---|---|
| Max page size | 100 (hard cap) |
| JWT access token TTL | 30 min |
| JWT refresh token TTL | 30 days |
| UUID primary keys | `CHAR(36)`, set by app — never `AUTO_INCREMENT` |
| Soft delete | `is_active TINYINT(1)` — never physically delete without `/permanent` |
| System roles | `SUPER_ADMIN`, `ADMIN`, `USER` — never modify or delete |
| Refresh token storage | SHA-256 hash only — never store plaintext |
| Async audit | `@Async("auditTaskExecutor")` — do not call audit methods synchronously |
| MDC context | `requestId`, `userId` auto-propagated — do not repeat in log messages |

---

## Build & Test Commands

```bash
./mvnw compile
./mvnw test
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
./mvnw package -DskipTests
```

---

## Key Directories

| Path | Contents |
|---|---|
| `src/main/java/org/ganjp/api/` | Main source code |
| `src/main/resources/` | `application*.yml` config files |
| `tooling/docs/context/` | AI context files (quick-reference, decisions, todo) |
| `tooling/docs/design/` | Per-module design documentation |
| `tooling/docs/guide/` | Style guide, AI coding guide |
| `tooling/scripts/database/mysql/` | SQL schema scripts |

---

## Git Conventions

- Commit messages: imperative mood, describe the "why"
- Do not commit `.env`, credentials, or secrets
