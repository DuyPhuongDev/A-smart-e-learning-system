# User Management Service

## Overview

The User Management Service is a core microservice in the LMS system responsible for managing user accounts, roles, and permissions. It implements a hybrid RBAC (Role-Based Access Control) system with endpoint-level permission management.

## Features

### Auto Response Wrapping 🎁
- ✅ **GlobalResponseHandler** tự động wrap responses vào `ApiResponse` format
- ✅ Controllers return data trực tiếp, không cần manual wrapping
- ✅ Consistent response format với status, message, data, timestamp, path
- ✅ See [RESPONSE_WRAPPING.md](./RESPONSE_WRAPPING.md) for details

### User Management
- ✅ CRUD operations for user accounts
- ✅ Extended user profiles (avatar, phone, address, department, student ID)
- ✅ Lock/Unlock user accounts
- ✅ Password reset coordination with Authentication Service
- ✅ Soft delete functionality
- ✅ Advanced filtering and pagination

### Role Management
- ✅ System predefined roles (STUDENT, TEACHER, ADMIN)
- ✅ Custom role creation and management
- ✅ Multiple roles per user
- ✅ Role assignment/removal

### Permission Management
- ✅ Endpoint-level permissions
- ✅ Automatic endpoint scanning from registered services
- ✅ Granular permission control (select/deselect endpoints for roles)
- ✅ Permission inheritance through roles

### Import/Export
- ✅ Excel (.xlsx) user import with validation
- ✅ Excel export with filters
- ✅ Template generation for import

## Technology Stack

- **Framework**: Spring Boot 3.5.0
- **Language**: Java 21
- **Database**: H2 (dev) / PostgreSQL (production ready)
- **Migration**: Flyway
- **Service Discovery**: Eureka Client
- **Inter-service Communication**: OpenFeign
- **Circuit Breaker**: Resilience4j
- **Object Mapping**: MapStruct
- **Excel Processing**: Apache POI
- **Validation**: Jakarta Validation

## Architecture

### Clean Architecture Layers

```
┌─────────────────────────────────────────┐
│          Controller Layer               │
│  (REST APIs, Request/Response handling) │
├─────────────────────────────────────────┤
│          Service Layer                  │
│     (Business logic, Orchestration)     │
├─────────────────────────────────────────┤
│         Repository Layer                │
│      (Data access with JPA)             │
├─────────────────────────────────────────┤
│          Model Layer                    │
│  (Entities, DTOs, Enums, Mappers)       │
└─────────────────────────────────────────┘
```

### Database Schema

**Main Entities:**
- `users` - User profile information
- `roles` - System and custom roles
- `user_roles` - Many-to-many relationship
- `permissions` - Role-endpoint permissions
- `endpoint_registry` - System endpoints catalog

## API Endpoints

### User Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users (paginated, filterable) |
| GET | `/api/users/{id}` | Get user details |
| POST | `/api/users` | Create new user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user (soft delete) |
| PATCH | `/api/users/{id}/lock` | Lock user account |
| PATCH | `/api/users/{id}/unlock` | Unlock user account |
| POST | `/api/users/{id}/reset-password` | Reset user password |

### User-Role Assignment

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/{userId}/roles` | Assign role to user |
| DELETE | `/api/users/{userId}/roles/{roleId}` | Remove role from user |
| GET | `/api/users/{userId}/roles` | Get user's roles |

### Import/Export

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/import` | Import users from Excel |
| GET | `/api/users/export` | Export users to Excel |
| GET | `/api/users/import/template` | Download import template |

### Role Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/roles` | Get all roles |
| GET | `/api/roles/{id}` | Get role details with permissions |
| POST | `/api/roles` | Create custom role |
| PUT | `/api/roles/{id}` | Update custom role |
| DELETE | `/api/roles/{id}` | Delete custom role |

### Permission Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/permissions/endpoints` | Get all system endpoints |
| POST | `/api/permissions/endpoints/scan` | Scan endpoints from services |
| PUT | `/api/permissions/roles/{roleId}` | Update role permissions |
| GET | `/api/permissions/roles/{roleId}` | Get role permissions |

## Request/Response Examples

### Create User

**Request:**
```json
POST /api/users
{
  "email": "student@hcmut.edu.vn",
  "fullName": "Nguyen Van A",
  "phone": "0901234567",
  "address": "Ho Chi Minh City",
  "department": "Computer Science",
  "studentId": "2110001",
  "roleIds": ["<uuid-of-student-role>"]
}
```

**Response (Auto-wrapped):**
```json
{
  "status": 200,
  "message": "Success",
  "timestamp": "2024-01-15T10:30:00",
  "path": "/api/users",
  "data": {
    "id": "uuid",
    "email": "student@hcmut.edu.vn",
    "fullName": "Nguyen Van A",
    "avatar": null,
    "status": "ACTIVE",
    "roles": [
      {
        "id": "uuid",
        "name": "STUDENT",
        "description": "Student role with basic permissions",
        "type": "SYSTEM_PREDEFINED",
        "isActive": true
      }
    ]
  }
}
```

### Update Role Permissions

**Request:**
```json
PUT /api/permissions/roles/{roleId}
{
  "permissions": [
    {
      "endpointId": "<endpoint-uuid>",
      "isAllowed": true
    },
    {
      "endpointId": "<endpoint-uuid>",
      "isAllowed": false
    }
  ]
}
```

## Configuration

### application.yml

Key configurations:
- Port: 8082
- Database: H2 in-memory (dev)
- Flyway: Enabled with baseline
- File upload: Max 10MB
- Feign timeout: 5s
- Circuit breaker configured for auth service

## Database Migration

Flyway migrations are located in `src/main/resources/db/migration/`:

1. V1 - Create users table
2. V2 - Create roles table
3. V3 - Create user_roles junction table
4. V4 - Create endpoint_registry table
5. V5 - Create permissions table
6. V6 - Insert predefined roles (STUDENT, TEACHER, ADMIN)

## Data Initialization

On startup, the `DataInitializer` component:
- Creates default endpoints for all services
- Sets up default permissions for predefined roles:
  - **ADMIN**: Full access to all endpoints
  - **TEACHER**: Access to courses, assessments, read users
  - **STUDENT**: Read-only access to courses and assessments

## Integration with Authentication Service

The User Management Service communicates with the Authentication Service via Feign client for:
- Creating user credentials (temporary password generated)
- Resetting passwords
- Locking/unlocking accounts

**Fallback mechanism**: If Authentication Service is unavailable, operations continue but credentials operations are logged as failed.

## Excel Import/Export

### Import Features
- Email validation and duplicate checking
- Required field validation
- Role assignment via comma-separated role names
- Detailed error reporting per row

### Export Features
- Filtered export (by status, department, search)
- All user fields included
- Excel format (.xlsx)

### Template Columns
1. Email (required)
2. Full Name (required)
3. Phone
4. Address
5. Department
6. Student ID
7. Roles (comma-separated)

## Security Considerations

1. **Password Management**: Passwords are never stored in this service. Only hashed passwords exist in Authentication Service.
2. **Soft Delete**: Users are marked as DELETED, not physically removed.
3. **Role Protection**: System predefined roles cannot be modified or deleted.
4. **Permission Validation**: All endpoint accesses can be validated through the permission checking mechanism.

## Error Handling

Custom exceptions with proper HTTP status codes:
- `UserNotFoundException` (404)
- `RoleNotFoundException` (404)
- `EndpointNotFoundException` (404)
- `DuplicateEmailException` (409)
- `DuplicateRoleNameException` (409)
- `SystemRoleModificationException` (403)
- `RoleInUseException` (409)
- `ExcelProcessingException` (400)

## Running the Service

### Prerequisites
- Java 21
- Maven 3.6+
- Eureka Server running on port 8761

### Start the service
```bash
cd user-management-service
mvn spring-boot:run
```

### Access H2 Console (Development)
- URL: http://localhost:8082/h2-console
- JDBC URL: jdbc:h2:mem:userdb
- Username: sa
- Password: (empty)

## Testing

### Manual Testing with Postman/cURL

1. **Create a user:**
```bash
curl -X POST http://localhost:8082/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "fullName": "Test User",
    "roleIds": []
  }'
```

2. **Get all users:**
```bash
curl http://localhost:8082/api/users?page=0&size=10
```

3. **Scan endpoints:**
```bash
curl -X POST http://localhost:8082/api/permissions/endpoints/scan
```

## Future Enhancements

- [ ] Implement comprehensive unit and integration tests
- [ ] Add API documentation with Swagger/OpenAPI
- [ ] Implement audit logging for all operations
- [ ] Add user profile picture upload to cloud storage
- [ ] Implement advanced RBAC with hierarchical roles
- [ ] Add bulk operations (bulk delete, bulk role assignment)
- [ ] Implement user activity tracking
- [ ] Add support for CSV import/export
- [ ] Implement real-time endpoint discovery via Actuator

## Contributing

When adding new features:
1. Follow the existing package structure
2. Create DTOs for all request/response
3. Implement service layer before controller
4. Add validation annotations
5. Handle exceptions properly
6. Update Flyway migrations if schema changes
7. Update this README

## License

Part of the LMS-HCMUT Backend System

