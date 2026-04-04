# CLAUDE.md — Project Instructions for Claude Code

## Project Overview

GJP Admin API — a Spring Boot 3.x REST API with JWT authentication, role-based authorization, async audit logging, and modular architecture.

## AI Context Files

Read these before starting any task:

- [**Quick Reference**](tooling/docs/context/quick-reference.md) — all entities, endpoints, and mandatory patterns in one page
- [**Decisions**](tooling/docs/context/decisions.md) — why key architectural choices were made; do not re-propose items listed here
- [**TODO**](tooling/docs/context/todo.md) — in-progress work and known gaps; check before starting new features

## Coding Rules

Follow the rules in [tooling/docs/guide/AI_CODING_GUIDE.md](tooling/docs/guide/AI_CODING_GUIDE.md). For full rationale and examples, see [tooling/docs/guide/STYLE_GUIDE.md](tooling/docs/guide/STYLE_GUIDE.md).

## Build & Test Commands

```bash
# Compile
./mvnw compile

# Run tests
./mvnw test

# Run application (dev profile)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Package
./mvnw package -DskipTests
```

## Key Directories

- `src/main/java/org/ganjp/api/` — Main source code
- `src/main/resources/` — Configuration files (application*.yml)
- `tooling/scripts/database/mysql/` — SQL schema scripts
- `tooling/docs/guide/` — Style guide, AI coding guide
- `tooling/docs/design/` — Module design documentation (auth, etc.)

## Important Patterns

- All responses: `ResponseEntity<ApiResponse<T>>`
- All entities extend `BaseEntity` (audit fields + UUID)
- All exceptions handled by `GlobalExceptionHandler`
- Async audit via `@Async("auditTaskExecutor")`
- MDC context (requestId, userId) propagated automatically — don't repeat in log messages

## Git Conventions

- Commit messages: imperative mood, describe the "why"
- Don't commit `.env`, credentials, or secrets
