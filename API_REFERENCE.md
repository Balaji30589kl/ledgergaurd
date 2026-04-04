# API Reference

Base URL: `http://localhost:8085`

## Authentication

This API uses request header based authentication.

Header:
```http
X-USER-ID: <user-id>
```

Use one of the seeded users below while testing:

| Role | Example User ID | Access |
|---|---:|---|
| ADMIN | `4` | Full access to users, records, and dashboards |
| ANALYST | `1` | Read access to records and dashboards |
| VIEWER | `6` | Dashboard access only |

## Roles and Access

| Module | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| User Management | No | No | Yes |
| Record Read | No | Yes | Yes |
| Record Create / Update / Delete | No | No | Yes |
| Dashboard Summary / Trends | Yes | Yes | Yes |

## Error Response Format

Validation, authentication, authorization, and not-found responses use Spring `ProblemDetail` style error payloads. [web:15][web:110]

Typical error response:
```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "instance": "/api/records",
  "fieldErrors": {
    "amount": "must be greater than 0",
    "category": "must not be blank"
  }
}
```

Common status codes:

| Status | Meaning |
|---|---|
| `200 OK` | Request succeeded |
| `201 Created` | Resource created successfully |
| `204 No Content` | Resource deleted successfully |
| `400 Bad Request` | Validation failure |
| `401 Unauthorized` | Missing or invalid `X-USER-ID` |
| `403 Forbidden` | Authenticated user does not have access |
| `404 Not Found` | Resource does not exist |

---

# User Management

User management endpoints are restricted to `ADMIN`.

## List Users

`GET /api/users`

Headers:
```http
X-USER-ID: 4
```

Response:
- `200 OK`

Example:
```bash
curl -H "X-USER-ID: 4" http://localhost:8085/api/users
```

Example response:
```json
[
  {
    "id": 1,
    "username": "Alice Analyst",
    "email": "alice@example.com",
    "role": "ANALYST",
    "status": "ACTIVE"
  }
]
```

## Create User

`POST /api/users`

Headers:
```http
X-USER-ID: 4
Content-Type: application/json
```

Request body:
```json
{
  "username": "Jane Doe",
  "email": "jane@example.com",
  "role": "VIEWER"
}
```

Response:
- `201 Created`
- `400 Bad Request`

Example:
```bash
curl -X POST http://localhost:8085/api/users \
  -H "X-USER-ID: 4" \
  -H "Content-Type: application/json" \
  -d '{"username":"Jane Doe","email":"jane@example.com","role":"VIEWER"}'
```

Example response:
```json
{
  "id": 10,
  "username": "Jane Doe",
  "email": "jane@example.com",
  "role": "VIEWER",
  "status": "ACTIVE"
}
```

## Update User

`PUT /api/users/{id}`

Headers:
```http
X-USER-ID: 4
Content-Type: application/json
```

Request body:
```json
{
  "username": "Jane Doe Updated",
  "role": "ANALYST"
}
```

Response:
- `200 OK`
- `400 Bad Request`
- `404 Not Found`

Example:
```bash
curl -X PUT http://localhost:8085/api/users/10 \
  -H "X-USER-ID: 4" \
  -H "Content-Type: application/json" \
  -d '{"username":"Jane Doe Updated","role":"ANALYST"}'
```

---

# Financial Records

Record access is role-based:
- `ADMIN`: create, read, update, delete
- `ANALYST`: read only
- `VIEWER`: no record access

## List Records

`GET /api/records`

Headers:
```http
X-USER-ID: 1
```

Response:
- `200 OK`
- `403 Forbidden`

Example:
```bash
curl -H "X-USER-ID: 1" http://localhost:8085/api/records
```

## Filter Records

`GET /api/records?date=2026-04-04&category=Utilities&type=EXPENSE`

Supported filters:
- `date`
- `category`
- `type`

Headers:
```http
X-USER-ID: 1
```

Response:
- `200 OK`

Example:
```bash
curl -H "X-USER-ID: 1" "http://localhost:8085/api/records?date=2026-04-04&category=Utilities&type=EXPENSE"
```

## Get Record By ID

`GET /api/records/{id}`

Headers:
```http
X-USER-ID: 1
```

Response:
- `200 OK`
- `404 Not Found`

Example:
```bash
curl -H "X-USER-ID: 1" http://localhost:8085/api/records/1
```

## Create Record

`POST /api/records`

Headers:
```http
X-USER-ID: 4
Content-Type: application/json
```

Request body:
```json
{
  "type": "EXPENSE",
  "amount": 250.00,
  "category": "Utilities",
  "date": "2026-04-04",
  "note": "Electricity bill"
}
```

Response:
- `201 Created`
- `400 Bad Request`
- `403 Forbidden`

Example:
```bash
curl -X POST http://localhost:8085/api/records \
  -H "X-USER-ID: 4" \
  -H "Content-Type: application/json" \
  -d '{"type":"EXPENSE","amount":250.00,"category":"Utilities","date":"2026-04-04","note":"Electricity bill"}'
```

Example response:
```json
{
  "id": 12,
  "type": "EXPENSE",
  "amount": 250.00,
  "category": "Utilities",
  "date": "2026-04-04",
  "note": "Electricity bill"
}
```

## Update Record

`PUT /api/records/{id}`

Headers:
```http
X-USER-ID: 4
Content-Type: application/json
```

Request body:
```json
{
  "type": "EXPENSE",
  "amount": 275.00,
  "category": "Utilities",
  "date": "2026-04-04",
  "note": "Updated electricity bill"
}
```

Response:
- `200 OK`
- `400 Bad Request`
- `404 Not Found`

Example:
```bash
curl -X PUT http://localhost:8085/api/records/12 \
  -H "X-USER-ID: 4" \
  -H "Content-Type: application/json" \
  -d '{"type":"EXPENSE","amount":275.00,"category":"Utilities","date":"2026-04-04","note":"Updated electricity bill"}'
```

## Delete Record

`DELETE /api/records/{id}`

Headers:
```http
X-USER-ID: 4
```

Response:
- `204 No Content`
- `404 Not Found`

Example:
```bash
curl -X DELETE -H "X-USER-ID: 4" http://localhost:8085/api/records/12
```

---

# Dashboard APIs

Dashboard APIs are available to `VIEWER`, `ANALYST`, and `ADMIN`.

## Dashboard Summary

`GET /api/dashboard/summary`

Headers:
```http
X-USER-ID: 6
```

Response:
- `200 OK`

Example:
```bash
curl -H "X-USER-ID: 6" http://localhost:8085/api/dashboard/summary
```

Typical response:
```json
{
  "totalIncome": 10000.00,
  "totalExpense": 4500.00,
  "netBalance": 5500.00
}
```

## Dashboard Trends

`GET /api/dashboard/trends?period=WEEK`

Supported values:
- `WEEK`
- `MONTH`

Headers:
```http
X-USER-ID: 6
```

Response:
- `200 OK`

Example:
```bash
curl -H "X-USER-ID: 6" "http://localhost:8085/api/dashboard/trends?period=WEEK"
```

Typical response:
```json
[
  {
    "label": "2026-04-01",
    "income": 1200.00,
    "expense": 300.00
  }
]
```

---

# Validation Rules

## User requests
- `username` must not be blank
- `email` must be a valid email
- `role` must be one of `ADMIN`, `ANALYST`, or `VIEWER`

## Financial record requests
- `type` must be one of `INCOME` or `EXPENSE`
- `amount` must be greater than `0`
- `category` must not be blank
- `date` must not be in the future

---

# Verification Notes

The API was verified end-to-end across user management, record management, dashboard access, validation, and role-based authorization flows. Error handling follows a consistent `ProblemDetail` response structure for bad request, unauthorized, forbidden, and not-found cases. [web:15][web:110]

# Repository Links

- Project Repository: `https://github.com/Balaji30589kl/ledgergaurd`
- README: `https://github.com/Balaji30589kl/ledgergaurd/blob/main/README.md`
- API Reference: `https://github.com/Balaji30589kl/ledgergaurd/blob/main/API_REFERENCE.md`