# Master Module — API Reference

## Base URL

```
http://localhost:8083/api/v1/app-settings
```

## Response Envelope

All endpoints return:

```json
{
  "status": 200,
  "message": "...",
  "data": { ... },
  "meta": { "requestId": "uuid" }
}
```

Paginated endpoints return `data` as:

```json
{
  "content": [ ... ],
  "page": 0,
  "size": 20,
  "totalElements": 100
}
```

## AppSetting Response Object

```json
{
  "id": "uuid",
  "name": "site_title",
  "value": "GJP Platform",
  "lang": "EN",
  "isSystem": false,
  "isPublic": true,
  "createdAt": "2026-04-04T10:00:00",
  "updatedAt": "2026-04-04T10:00:00",
  "createdBy": "user-uuid",
  "updatedBy": "user-uuid"
}
```

---

## Read Endpoints

### `GET /v1/app-settings`

Get all settings with pagination and optional filtering.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:**

| Param | Type | Description |
|---|---|---|
| `searchTerm` | string | Contains search on `name` or `value` |
| `lang` | enum | Filter by `EN` or `ZH` |
| `isPublic` | boolean | Filter by public flag |
| `isSystem` | boolean | Filter by system flag |
| `page` | int | Page number (0-based, default 0) |
| `size` | int | Page size (default 20) |
| `sort` | string | Sort field (e.g., `name`, `updatedAt`) |

**Response 200:**
```json
{
  "data": {
    "content": [ { ...AppSetting... } ],
    "page": 0,
    "size": 20,
    "totalElements": 42
  }
}
```

---

### `GET /v1/app-settings/{id}`

Get a single setting by UUID.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Response 200:** AppSetting object

**Response 404:** Setting not found

---

### `GET /v1/app-settings/by-name?name={name}&lang={lang}`

Get a single setting by its name and language.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `name` (required), `lang` (required: `EN` or `ZH`)

**Response 200:** AppSetting object

**Response 404:** Setting not found

---

### `GET /v1/app-settings/by-name/{name}`

Get all language variants for a setting name.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Response 200:** Array of AppSetting objects (one per language that exists)

```json
{
  "data": [
    { "name": "site_title", "value": "GJP Platform", "lang": "EN" },
    { "name": "site_title", "value": "GJP平台", "lang": "ZH" }
  ]
}
```

---

### `GET /v1/app-settings/by-language/{lang}`

Get all settings in a specific language.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Path variable:** `lang` — `EN` or `ZH`

**Response 200:** Array of AppSetting objects

---

### `GET /v1/app-settings/public`

Get all settings with `is_public = true`. Does **not** require authentication.

**Auth:** None

**Response 200:** Array of AppSetting objects (both languages)

---

### `GET /v1/app-settings/public/{lang}`

Get public settings filtered by language. Does **not** require authentication.

**Auth:** None

**Path variable:** `lang` — `EN` or `ZH`

**Response 200:** Array of AppSetting objects

---

### `GET /v1/app-settings/user-editable`

Get all settings with `is_system = false` (editable by users).

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Response 200:** Array of AppSetting objects

---

### `GET /v1/app-settings/user-editable/{lang}`

Get user-editable settings filtered by language.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Path variable:** `lang` — `EN` or `ZH`

**Response 200:** Array of AppSetting objects

---

### `GET /v1/app-settings/value?name={name}&lang={lang}&defaultValue={default}`

Get only the `value` string of a named setting.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `name` (required), `lang` (required), `defaultValue` (optional — returned if setting not found)

**Response 200:**
```json
{
  "data": "GJP Platform"
}
```

---

### `GET /v1/app-settings/names`

Get the list of distinct setting names (all names that have at least one language entry).

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Response 200:**
```json
{
  "data": ["site_title", "footer_text", "contact_email"]
}
```

---

### `GET /v1/app-settings/statistics`

Get aggregate statistics about settings.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Response 200:**
```json
{
  "data": {
    "totalCount": 42,
    "systemCount": 5,
    "publicCount": 12,
    "enCount": 21,
    "zhCount": 21
  }
}
```

---

### `GET /v1/app-settings/exists?name={name}&lang={lang}`

Check whether a setting exists for the given name and language.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Response 200:**
```json
{
  "data": true
}
```

---

## Write Endpoints

### `POST /v1/app-settings`

Create a new setting.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Request body:**
```json
{
  "name": "site_title",
  "value": "GJP Platform",
  "lang": "EN",
  "isSystem": false,
  "isPublic": true
}
```

**Validation:**
- `name`: required, max 50 characters
- `lang`: required, must be `EN` or `ZH`
- `value`: optional, max 500 characters
- Uniqueness: `(name, lang)` must not already exist

**Response 201:** Created AppSetting object

---

### `PUT /v1/app-settings/{id}`

Update a setting by ID.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Request body:**
```json
{
  "name": "site_title",
  "value": "New Title",
  "lang": "EN",
  "isSystem": false,
  "isPublic": true
}
```

**Response 200:** Updated AppSetting object

---

### `PUT /v1/app-settings/value?name={name}&lang={lang}&value={value}`

Update only the `value` of a setting identified by name and language.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `name` (required), `lang` (required), `value` (required)

**Response 200:** Updated AppSetting object

---

## Delete Endpoints

### `DELETE /v1/app-settings/{id}`

Delete a setting by UUID.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Response 200:**
```json
{
  "data": "Setting deleted",
  "message": "App setting deleted successfully"
}
```

---

### `DELETE /v1/app-settings/by-name?name={name}&lang={lang}`

Delete a setting identified by name and language.

**Auth:** `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`

**Query params:** `name` (required), `lang` (required)

**Response 200**

---

## Full Endpoint Summary

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/v1/app-settings` | ADMIN+ | Paginated search |
| `GET` | `/v1/app-settings/{id}` | ADMIN+ | By ID |
| `GET` | `/v1/app-settings/by-name` | ADMIN+ | By name + lang (query params) |
| `GET` | `/v1/app-settings/by-name/{name}` | ADMIN+ | All langs for a name |
| `GET` | `/v1/app-settings/by-language/{lang}` | ADMIN+ | All settings in a language |
| `GET` | `/v1/app-settings/public` | None | All public settings |
| `GET` | `/v1/app-settings/public/{lang}` | None | Public settings by language |
| `GET` | `/v1/app-settings/user-editable` | ADMIN+ | User-editable settings |
| `GET` | `/v1/app-settings/user-editable/{lang}` | ADMIN+ | User-editable by language |
| `GET` | `/v1/app-settings/value` | ADMIN+ | Raw value string |
| `GET` | `/v1/app-settings/names` | ADMIN+ | Distinct setting names |
| `GET` | `/v1/app-settings/statistics` | ADMIN+ | Statistics |
| `GET` | `/v1/app-settings/exists` | ADMIN+ | Existence check |
| `POST` | `/v1/app-settings` | ADMIN+ | Create setting |
| `PUT` | `/v1/app-settings/{id}` | ADMIN+ | Update by ID |
| `PUT` | `/v1/app-settings/value` | ADMIN+ | Update value by name+lang |
| `DELETE` | `/v1/app-settings/{id}` | ADMIN+ | Delete by ID |
| `DELETE` | `/v1/app-settings/by-name` | ADMIN+ | Delete by name+lang |

> **ADMIN+** means `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`.

---

## Error Codes

| HTTP Status | Condition |
|---|---|
| `400 Bad Request` | Validation failure (e.g., name too long, invalid lang) |
| `401 Unauthorized` | Missing or expired JWT |
| `403 Forbidden` | Insufficient role |
| `404 Not Found` | Setting ID or name+lang not found |
| `409 Conflict` | `(name, lang)` already exists on create |

---

*Last Updated: 2026-04-04*
