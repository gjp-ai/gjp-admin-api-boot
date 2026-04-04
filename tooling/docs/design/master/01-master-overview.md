# Master Module — Architecture Overview

## Purpose

The Master module manages application-wide configuration data. Currently it contains one sub-module: `setting`, which stores named key-value pairs with internationalisation (i18n) support and visibility controls.

---

## Sub-Modules

| Sub-module | Entity | Table | Description |
|---|---|---|---|
| `setting` | `AppSetting` | `master_app_settings` | Application settings with per-language values, system/public flags |

---

## AppSetting Entity

**Package:** `org.ganjp.api.master.setting`

**Table:** `master_app_settings`

### Fields

| Field | Column | Type | Default | Description |
|---|---|---|---|---|
| `id` | `id` | `VARCHAR(36)` PK | — | UUID primary key |
| `name` | `name` | `VARCHAR(50)` NOT NULL | — | Setting key (e.g., `site_title`, `footer_text`) |
| `value` | `value` | `VARCHAR(500)` | NULL | Setting value for the given language |
| `lang` | `lang` | `ENUM('EN','ZH')` NOT NULL | `'EN'` | Language for this value |
| `isSystem` | `is_system` | `TINYINT(1)` NOT NULL | `0` | If `true`, the setting is reserved by the system and not user-editable |
| `isPublic` | `is_public` | `TINYINT(1)` NOT NULL | `0` | If `true`, the setting is accessible via the public (unauthenticated) endpoint |
| `createdAt` | `created_at` | `DATETIME` NOT NULL | auto | Set on insert |
| `updatedAt` | `updated_at` | `DATETIME` NOT NULL | auto | Updated on update |
| `createdBy` | `created_by` | `CHAR(36)` | NULL | userId of creator |
| `updatedBy` | `updated_by` | `CHAR(36)` | NULL | userId of last updater |

### Business Methods

| Method | Logic |
|---|---|
| `isUserEditable()` | Returns `!isSystem` — system settings cannot be modified by regular admin operations |
| `isPublicVisible()` | Returns `isPublic` — public settings are exposed without authentication |

---

## Design Decisions

### Per-Language Rows

Each `(name, lang)` pair is a separate row. To provide a setting in both English and Chinese, insert two rows:

```
name="site_title", lang="EN", value="GJP Platform"
name="site_title", lang="ZH", value="GJP平台"
```

This avoids nullable columns or JSON blobs for translations, keeping queries simple and indexed.

### System vs User-Editable Settings

- `is_system = true` — reserved settings (e.g., internal flags, infrastructure config). The API prevents direct modification via normal update endpoints.
- `is_system = false` — user-editable. Exposed via `GET /v1/app-settings/user-editable`.

### Public vs Private Settings

- `is_public = true` — exposed via `GET /v1/app-settings/public` without requiring authentication. Suitable for UI configuration consumed by a frontend without a user login.
- `is_public = false` — requires `ROLE_ADMIN` or `ROLE_SUPER_ADMIN` to read.

---

## Authorization Model

| Operation | Required Role |
|---|---|
| Read (paginated/filtered) | `ROLE_ADMIN` or `ROLE_SUPER_ADMIN` |
| Read by ID / name / language | `ROLE_ADMIN` or `ROLE_SUPER_ADMIN` |
| Read public settings | None (unauthenticated) |
| Read user-editable settings | `ROLE_ADMIN` or `ROLE_SUPER_ADMIN` |
| Read setting value | `ROLE_ADMIN` or `ROLE_SUPER_ADMIN` |
| Create | `ROLE_ADMIN` or `ROLE_SUPER_ADMIN` |
| Update (by ID or by name+lang) | `ROLE_ADMIN` or `ROLE_SUPER_ADMIN` |
| Delete (by ID or by name+lang) | `ROLE_ADMIN` or `ROLE_SUPER_ADMIN` |

> Note: Unlike CMS modules, `ROLE_SUPER_ADMIN` is not required exclusively for delete — both `ROLE_ADMIN` and `ROLE_SUPER_ADMIN` can delete settings.

---

## Typical Use Cases

| Setting Name | `is_system` | `is_public` | Purpose |
|---|---|---|---|
| `site_title` | false | true | Page title shown in the browser tab |
| `footer_text` | false | true | Footer copyright text |
| `contact_email` | false | true | Public contact address |
| `maintenance_mode` | true | false | Internal flag checked by application logic |
| `max_upload_size_mb` | true | false | Infrastructure config, not user-editable |
| `welcome_message` | false | true | Displayed on the landing page |

---

## Dependency Graph

```
AppSettingController (/v1/app-settings)
        │
        ▼
AppSettingService
        │
        ├── AppSettingRepository  (Spring Data JPA)
        └── JwtUtils              (extract userId for audit)
```

---

*Last Updated: 2026-04-04*
