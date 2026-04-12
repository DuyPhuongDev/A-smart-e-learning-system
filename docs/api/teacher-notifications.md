# Teacher Notification API

Base URL: `/api/v1/notifications`

All endpoints require the caller to be authenticated as a **TEACHER** role. Requests are routed through the API gateway with the `Authorization: Bearer <token>` header.

---

## Enums

| Enum | Values |
|------|--------|
| `NotificationType` | `SYSTEM_ANNOUNCEMENT`, `ASSIGNMENT_CREATED`, `DEADLINE_REMINDER`, `SUBMISSION_GRADED`, `DISCUSSION_REPLY`, `SYSTEM_MAINTENANCE` |
| `NotificationPriority` | `LOW`, `MEDIUM`, `HIGH`, `URGENT` |
| `NotificationChannel` | `IN_APP`, `EMAIL`, `PUSH` |
| `SendMode` | `DRAFT`, `IMMEDIATE`, `SCHEDULED` |
| `NotificationStatus` | `DRAFT`, `SCHEDULED`, `SENT`, `FAILED`, `CANCELLED` |
| `TeacherNotificationScope` | `ALL_MY_CLASSES`, `SPECIFIC_CLASSES` |

---

## 1. Create Notification

`POST /api/v1/notifications/teacher`

Creates a new notification targeting students in the teacher's classes.

### Headers

| Header | Required | Description |
|--------|----------|-------------|
| `Authorization` | Yes | `Bearer <token>` |
| `Idempotency-Key` | No | Unique string to prevent duplicate submissions on retry |

### Request Body

```json
{
  "title": "string (required, non-blank)",
  "content": "string (required, non-blank)",
  "type": "NotificationType (required)",
  "priority": "NotificationPriority (required)",
  "scope": "TeacherNotificationScope (required)",
  "classIds": ["uuid", "..."],
  "channels": ["NotificationChannel", "..."],
  "sendMode": "SendMode (required)",
  "scheduledAt": "ISO-8601 datetime (required if sendMode=SCHEDULED)",
  "expiresAt": "ISO-8601 datetime (optional)"
}
```

**Field rules:**
- `scope = ALL_MY_CLASSES` — `classIds` is ignored; the notification goes to all students in every class the teacher manages.
- `scope = SPECIFIC_CLASSES` — `classIds` is required and must only contain IDs of classes managed by the authenticated teacher. Providing a class not owned by the teacher returns `403 Forbidden`.
- `channels` must be a non-empty array.
- `scheduledAt` must be a future datetime when provided.
- `expiresAt` must be after `scheduledAt` when both are provided.

### Example — send immediately to all classes

```json
{
  "title": "Midterm reminder",
  "content": "Your midterm exam is next Monday. Please review chapters 1–5.",
  "type": "DEADLINE_REMINDER",
  "priority": "HIGH",
  "scope": "ALL_MY_CLASSES",
  "channels": ["IN_APP", "EMAIL"],
  "sendMode": "IMMEDIATE"
}
```

### Example — schedule to specific classes

```json
{
  "title": "Assignment 3 released",
  "content": "Assignment 3 is now available. Deadline: 2026-05-01.",
  "type": "ASSIGNMENT_CREATED",
  "priority": "MEDIUM",
  "scope": "SPECIFIC_CLASSES",
  "classIds": [
    "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
  ],
  "channels": ["IN_APP"],
  "sendMode": "SCHEDULED",
  "scheduledAt": "2026-04-15T08:00:00+07:00"
}
```

### Response `200 OK`

```json
{
  "notificationId": "uuid",
  "status": "NotificationStatus"
}
```

### Error responses

| Status | Reason |
|--------|--------|
| `400` | Missing required fields, invalid enum values, or bad datetime format |
| `403` | Caller is not a TEACHER, or `classIds` contains classes not managed by the teacher |
| `400` | Teacher manages no classes (`scope = ALL_MY_CLASSES` with empty class list) |

---

## 2. List Notifications

`GET /api/v1/notifications/teacher`

Returns a paginated list of notifications created by the authenticated teacher.

### Query Parameters

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `status` | `NotificationStatus` | — | Filter by status |
| `type` | `NotificationType` | — | Filter by type |
| `channel` | `NotificationChannel` | — | Filter by channel (e.g. `IN_APP`) |
| `from` | ISO-8601 datetime | — | Filter `createdAt >=` |
| `to` | ISO-8601 datetime | — | Filter `createdAt <=` |
| `keyword` | string | — | Full-text search on title and content |
| `page` | int | `0` | Zero-based page number |
| `size` | int | `10` | Page size |

### Response `200 OK`

```json
{
  "content": [
    {
      "id": "uuid",
      "title": "string",
      "type": "NotificationType",
      "priority": "NotificationPriority",
      "status": "NotificationStatus",
      "channels": ["string"],
      "createdAt": "ISO-8601",
      "scheduledAt": "ISO-8601 | null",
      "expiresAt": "ISO-8601 | null",
      "totalRecipients": 0,
      "deliveredCount": 0,
      "failedCount": 0,
      "readCount": 0
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 0,
  "totalPages": 0,
  "first": true,
  "last": true,
  "empty": true
}
```

---

## 3. Get Notification Detail

`GET /api/v1/notifications/teacher/{id}`

### Path Parameter

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | UUID | Notification ID |

### Response `200 OK`

```json
{
  "id": "uuid",
  "title": "string",
  "content": "string",
  "type": "NotificationType",
  "priority": "NotificationPriority",
  "status": "NotificationStatus",
  "targetMode": "COURSE",
  "targetPayload": {
    "classIds": ["uuid", "..."]
  },
  "channels": ["string"],
  "metadata": {
    "source": "teacher",
    "createdBy": "uuid",
    "createdAt": "ISO-8601",
    "scope": "ALL_MY_CLASSES | SPECIFIC_CLASSES"
  },
  "scheduledAt": "ISO-8601 | null",
  "expiresAt": "ISO-8601 | null",
  "createdAt": "ISO-8601",
  "updatedAt": "ISO-8601",
  "totalRecipients": 0,
  "deliveredCount": 0,
  "failedCount": 0,
  "readCount": 0
}
```

### Error responses

| Status | Reason |
|--------|--------|
| `404` | Notification not found |
| `403` | Notification was not created by the authenticated teacher |

---

## 4. Send Now

`POST /api/v1/notifications/teacher/{id}/send-now`

Immediately dispatches a `DRAFT` or `SCHEDULED` notification.

### Path Parameter

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | UUID | Notification ID |

### Response `200 OK` — empty body

### Error responses

| Status | Reason |
|--------|--------|
| `400` | Notification is not in `DRAFT` or `SCHEDULED` status |
| `403` | Not the owner of the notification |
| `404` | Notification not found |

---

## 5. Cancel Notification

`POST /api/v1/notifications/teacher/{id}/cancel`

Cancels a `SCHEDULED` notification before it is sent.

### Path Parameter

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | UUID | Notification ID |

### Response `200 OK` — empty body

### Error responses

| Status | Reason |
|--------|--------|
| `400` | Notification is not in `SCHEDULED` status |
| `403` | Not the owner of the notification |
| `404` | Notification not found |

---

## Typical FE Flows

### Send an announcement immediately
1. `POST /teacher` with `sendMode: "IMMEDIATE"` → get `notificationId`
2. Optionally poll `GET /teacher/{id}` to check delivery stats

### Schedule a notification
1. `POST /teacher` with `sendMode: "SCHEDULED"` and `scheduledAt` → status returns `SCHEDULED`
2. Show in list with `GET /teacher?status=SCHEDULED`
3. Teacher can cancel with `POST /teacher/{id}/cancel` before the scheduled time
4. Or send early with `POST /teacher/{id}/send-now`

### Save as draft
1. `POST /teacher` with `sendMode: "DRAFT"` → status returns `DRAFT`
2. Teacher reviews later via `GET /teacher/{id}`
3. Send when ready with `POST /teacher/{id}/send-now`

### Idempotent submission (retry-safe)
1. Generate a unique `Idempotency-Key` (e.g. UUID v4) on the client before submitting
2. Include it as a request header: `Idempotency-Key: <key>`
3. If the network fails and the request is retried with the same key, the server returns the original result without creating a duplicate
