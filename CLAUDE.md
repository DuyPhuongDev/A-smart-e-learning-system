# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Learning Management System (LMS) backend built with Java 21 and Spring Boot 3.5.0 using a microservices architecture. It consists of 11 services: 3 infrastructure services (Eureka Server, API Gateway, Config Server) and 8 business services (Authentication, User Management, Course Management, Assessment, Learning, Personalization, Notification, Communication).

## Build and Development Commands

### Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL 16+ (or use Docker)

### Build Commands

```bash
# Build entire project (from root)
./mvnw clean install

# Build specific service
cd <service-name> && ../mvnw clean install

# Build skipping tests
./mvnw clean install -DskipTests

# Force update dependencies
./mvnw clean install -U
```

### Run Services

**Start single service:**
```bash
cd user-management-service
../mvnw spring-boot:run
```

## Service Architecture

### Service Ports

| Service | Port | Base Package |
|---------|------|--------------|
| Eureka Server | 8761 | - |
| API Gateway | 8080 | `com.hcmut.lms.gateway` |
| Config Server | 8888 | - |
| Authentication | 8081 | `com.hcmut.lms.authentication` |
| User Management | 8082 | `com.hcmut.lms.usermanagement` |
| Course Management | 8083 | `com.hcmut.lms.coursemanagement` |
| Assessment | 8084 | `com.hcmut.lms.assessment` |
| Learning | 8086 | `com.hcmut.lms.learning` |
| Personalization | 8087 | `com.hcmut.lms.personalization` |
| Notification | 8090 | `com.hcmut.lms.notification` |
| Communication | 8091 | `com.hcmut.lms.communication` |

### Database Architecture

- **Single shared PostgreSQL database** (`lms_db`) with schema separation per service
- Each service uses its own schema (e.g., `user_management`, `course_management`)
- Flyway manages database migrations per service
- Connection URL pattern: `jdbc:postgresql://localhost:5432/lms_db?currentSchema=<service_schema>`

### Inter-Service Communication

- **Service Discovery**: Netflix Eureka (port 8761)
- **API Gateway**: Spring Cloud Gateway (port 8080) with JWT authentication
- **Synchronous**: OpenFeign clients with Resilience4j circuit breaker
- **Internal endpoints**: `/api/<service>/internal/**` (blocked by API Gateway)

## Code Patterns

### Package Structure

```
<service>/src/main/java/com/hcmut/lms/<service>/
├── Application.java
├── client/                    # Feign clients for inter-service calls
├── config/                    # Configuration classes
├── controller/                # REST controllers
│   └── internal/             # Internal endpoints (service-to-service)
├── exception/                 # Custom exceptions
├── mapper/                    # MapStruct mappers
├── model/
│   ├── dto/request/          # Request DTOs
│   ├── dto/response/         # Response DTOs
│   └── entity/               # JPA entities
├── repository/                # Spring Data JPA repositories
├── service/                   # Service interfaces
│   └── impl/                 # Service implementations
└── util/                      # Utility classes
```

### Controller Pattern

```java
@RestController
@RequestMapping("/api/<resource>")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponse create(@Valid @RequestBody CreateResourceRequest request) { ... }
}
```

### Service Implementation Pattern

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    @Override
    @Transactional(readOnly = true)  // Read operations
    public ResourceResponse getById(UUID id) { ... }

    @Override
    @Transactional  // Write operations
    public ResourceResponse create(CreateResourceRequest request) { ... }
}
```

### Entity Pattern

```java
@Entity
@Table(name = "entities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

### MapStruct Mapper Pattern

```java
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EntityMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Entity toEntity(CreateEntityRequest request);

    EntityResponse toResponse(Entity entity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(UpdateEntityRequest request, @MappingTarget Entity entity);
}
```

### Feign Client Pattern

```java
@FeignClient(name = "<service-name>", fallback = <Service>ClientFallback.class)
public interface ServiceClient {
    @PostMapping("/api/<service>/internal/<endpoint>")
    void someInternalOperation(@RequestBody SomeRequest request);
}

@Component
@Slf4j
public class ServiceClientFallback implements ServiceClient {
    @Override
    public void someInternalOperation(SomeRequest request) {
        log.warn("Fallback triggered for service operation");
    }
}
```

### Repository Pattern

```java
@Repository
public interface EntityRepository extends JpaRepository<Entity, UUID> {
    Optional<Entity> findBySomeField(String someField);
    boolean existsBySomeField(String someField);
    List<Entity> findByCategoryOrderByCreatedAtDesc(String category);
}
```

### Exception Handling

Services use custom exceptions extending `RuntimeException`:
- `ResourceNotFoundException` - 404 errors
- `DuplicateResourceException` - 409 errors
- `ValidationException` - 400 errors

## Common Module

The `common` module contains shared utilities:
- `PageResponse` - Pagination wrapper
- `ApiResponse` / `ErrorResponse` - Standard response formats
- `CurrentUser` / `CurrentUserInfo` - Authentication context helpers
- `GlobalResponseHandler` - Global response handling

All business services depend on the `common` module.

## Configuration

### Application.yml Structure

Each service has its own `application.yml` with:
- Server port
- PostgreSQL datasource with service-specific schema
- JPA/Hibernate configuration (`ddl-auto: validate` - use Flyway for migrations)
- Eureka client configuration
- Flyway migration settings
- Service-specific settings (Feign timeouts, file upload limits, etc.)

### Environment Variables

```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=lms_db
DB_USER=lms_user
DB_PASSWORD=lms_password
JWT_SECRET=<secret-key>
```

## API Gateway Routing

Routes are defined in `api-gateway/src/main/resources/application.yml`:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-management-service
          uri: lb://user-management-service
          predicates:
            - Path=/api/users/**,/api/roles/**
```

Public endpoints (no JWT) are configured in `RouteValidator.java`.
Internal endpoints (`/api/*/internal/**`) are blocked by `InternalEndpointBlockFilter`.

## Development Workflow

1. Start infrastructure: Eureka Server first, then API Gateway
2. Start business services in any order (they register with Eureka)
3. Access API through Gateway (port 8080), not directly
4. Use `./check-services.sh` to verify all services are running
5. View Eureka Dashboard at http://localhost:8761

## Code Style

See `CODE_STYLE.md` for detailed conventions. Key points:
- Use Lombok annotations (`@Data`, `@Builder`, `@RequiredArgsConstructor`)
- Use MapStruct for DTO/entity mapping
- Return `ResponseEntity` or DTOs directly from controllers
- Use UUID for entity IDs
- Use `@Transactional(readOnly = true)` for read operations
- Log with SLF4J: `log.info("Action: {}", value)`
