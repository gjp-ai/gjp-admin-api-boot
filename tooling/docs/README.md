# GJP Admin API — Documentation Index

Welcome to the internal documentation for the GJP Admin API project. This index provides quick access to design documents, guides, and technical references.

## 🤖 AI Context

Compact files for fast AI orientation — read these first.

- [**Quick Reference**](./context/quick-reference.md) — all entities, endpoints, and patterns on one page.
- [**Decisions**](./context/decisions.md) — why key architectural choices were made (ADRs).
- [**TODO**](./context/todo.md) — in-progress work, known gaps, and items decided against.

## 🏗️ Architecture & Design

### Auth Module

Comprehensive details on how authentication, authorization, and security are built.

- [**Architecture Overview**](./design/auth/01-architecture-overview.md) — High-level system structure and components.
- [**Authentication Flow**](./design/auth/02-authentication-flow.md) — How login and token management works.
- [**JWT Token Design**](./design/auth/03-jwt-token-design.md) — Access and Refresh token specifications.
- [**Security Features**](./design/auth/04-security-features.md) — Rate limiting, locking, and defensive measures.
- [**Database Schema**](./design/auth/05-database-schema.md) — Entity relationships and table definitions.
- [**API Reference**](./design/auth/06-api-reference.md) — Detailed endpoint documentation.
- [**Role-Based Access Control (RBAC)**](./design/auth/07-role-based-access-control.md) — Hierarchy and permission levels.
- [**Audit & Configuration**](./design/auth/08-audit-and-configuration.md) — Logging and system settings.

### CMS Module

Content management for articles, media, and website directory.

- [**CMS Overview**](./design/cms/01-cms-overview.md) — Module structure, shared patterns, media storage, and external integrations.
- [**CMS Database Schema**](./design/cms/02-cms-database-schema.md) — Table definitions for all 9 CMS entities.
- [**CMS API Reference**](./design/cms/03-cms-api-reference.md) — All endpoints for articles, audio, files, images, logos, questions, videos, and websites.
- [**CMS Media Processing**](./design/cms/04-cms-media-processing.md) — Async download flows, logo processing, image resize, Range request streaming.

### Master Module

Application-wide configuration and settings management.

- [**Master Overview**](./design/master/01-master-overview.md) — AppSetting entity design, i18n strategy, system vs public flags.
- [**Master API Reference**](./design/master/02-master-api-reference.md) — All `/v1/app-settings` endpoints.

## 📖 Developer Guides

Standards and procedures for contributing to the codebase.

- [**Style Guide**](./guide/STYLE_GUIDE.md) — Java, JSON, and Markdown formatting standards.
- [**AI Coding Guide**](./guide/AI_CODING_GUIDE.md) — Best practices for working with AI assistants in this repo.

## 🛠️ Tools & Assets

- [**Postman Collections**](../postman/collections/) — Pre-configured requests for testing.
- [**Utility Scripts**](../scripts/util/) — DB setup, JWT generation, and application runner.

---
*Last Updated: 2026-04-04*
