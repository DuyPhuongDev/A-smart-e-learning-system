# Course Management Service Coding Style

This document captures the coding style observed in the course-management-service module.

## Project stack
- Java with Spring Boot
- Lombok for boilerplate reduction
- MapStruct for DTO/entity mapping
- Spring Data JPA repositories

## Package structure
- Base package: `com.hcmut.lms.coursemanagement`
- Layered subpackages (examples):
  - `application.dto.*`
  - `application.mapper`
  - `application.service` and `application.service.impl`
  - `controller` / `controller.internal`
  - `repository`
  - `domain.entity.*`

## Naming conventions
- Classes: `PascalCase`
- Methods/fields: `camelCase`
- IDs: `UUID` type for entities and DTOs

## Controllers
- Annotate with `@RestController`, `@RequestMapping`, `@RequiredArgsConstructor`.
- Return `ResponseEntity.ok(...)` for successful responses.
- Use `@Valid` for request validation when needed.
- Internal controllers live under `controller.internal` and use paths like `/api/courses/internal/...`.
- Short Javadoc blocks are used to describe internal endpoints.

## Services
- Interface in `application.service`, implementation in `application.service.impl`.
- Implementation annotated with `@Service`, `@RequiredArgsConstructor`, `@Slf4j`, `@Transactional`.
- Read operations use `@Transactional(readOnly = true)`.
- Log start and success messages with SLF4J placeholders, e.g.:
  - `log.info("Creating semester: {}", request.getSemesterCode());`

## Repositories
- Extend `JpaRepository<Entity, UUID>`.
- Use derived query methods when possible; `@Query` for custom queries.
- Return `Optional`, `List`, or `Page` as appropriate.

## DTOs
- Response DTOs use Lombok: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`.
- `@JsonInclude(JsonInclude.Include.NON_NULL)` on responses.
- Date fields are often represented as `String` in responses.

## Mapping (MapStruct)
- Mapper interfaces annotated with:
  - `@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)`
- Use `@Mapping` to:
  - Ignore IDs and relationships on create/update.
  - Map nested fields (e.g., `academicYear.id`).
  - Use `expression = "java(...)"` for formatted dates.
- Helper methods (default methods) inside mapper for computed fields.

## Pagination
- Use `PageRequest.of(page, size)` and map to `PageResponse.fromPage(...)`.

## Error handling
- Throw `EntityNotFoundException` with clear messages, e.g.:
  - `"Semester not found with id: " + id`
