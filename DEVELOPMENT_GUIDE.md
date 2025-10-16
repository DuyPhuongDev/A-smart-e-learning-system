# Development Guide

Hướng dẫn phát triển cho LMS Backend Microservices

## Mục Lục
1. [Setup Development Environment](#setup-development-environment)
2. [Project Structure](#project-structure)
3. [Coding Standards](#coding-standards)
4. [Adding a New Service](#adding-a-new-service)
5. [Adding a New Feature](#adding-a-new-feature)
6. [Testing Guidelines](#testing-guidelines)
7. [Database Migration](#database-migration)
8. [API Documentation](#api-documentation)
9. [Troubleshooting](#troubleshooting)

---

## Setup Development Environment

### Prerequisites
```bash
# Java 21
java -version

# Maven 3.9.9
mvn -version

# Git
git --version
```

### IDE Setup (IntelliJ IDEA)
1. Import project as Maven project
2. Enable annotation processing for Lombok
3. Install plugins:
   - Lombok
   - Spring Boot
   - Docker

### Clone and Build
```bash
git clone <repository-url>
cd Capstone_Project_LMS-HCMUT_BE
mvn clean install
```

---

## Project Structure

```
service-name/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hcmut/lms/{service}/
│   │   │       ├── controller/         # REST Controllers
│   │   │       ├── service/            # Business Logic
│   │   │       ├── repository/         # Data Access
│   │   │       ├── model/              # Domain Models
│   │   │       ├── dto/                # Data Transfer Objects
│   │   │       ├── config/             # Configurations
│   │   │       ├── exception/          # Custom Exceptions
│   │   │       └── util/               # Utility Classes
│   │   └── resources/
│   │       ├── application.yml         # Configuration
│   │       └── db/migration/           # Database Scripts
│   └── test/
│       └── java/                       # Unit & Integration Tests
└── pom.xml
```

---

## Coding Standards

### Naming Conventions
```java
// Classes - PascalCase
public class CourseService { }

// Methods - camelCase
public void createCourse() { }

// Constants - UPPER_SNAKE_CASE
public static final String API_VERSION = "v1";

// Variables - camelCase
private String courseName;
```

### Package Structure
```
com.hcmut.lms.{service}
├── controller      # REST endpoints
├── service         # Business logic
├── repository      # Data access
├── model           # Entities
├── dto             # DTOs
├── mapper          # DTO ↔ Entity mappers
├── config          # Configuration classes
└── exception       # Custom exceptions
```

### REST API Conventions
```java
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    
    // GET - Retrieve
    @GetMapping("/{id}")
    public ResponseDto<CourseDto> getCourse(@PathVariable Long id) { }
    
    // POST - Create
    @PostMapping
    public ResponseDto<CourseDto> createCourse(@RequestBody CourseDto dto) { }
    
    // PUT - Update
    @PutMapping("/{id}")
    public ResponseDto<CourseDto> updateCourse(@PathVariable Long id) { }
    
    // DELETE - Delete
    @DeleteMapping("/{id}")
    public ResponseDto<Void> deleteCourse(@PathVariable Long id) { }
}
```

### Response Format
```java
@Data
@Builder
public class ResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
}

// Success Response
return ResponseDto.<CourseDto>builder()
    .success(true)
    .message("Course created successfully")
    .data(courseDto)
    .build();

// Error Response
return ResponseDto.<Void>builder()
    .success(false)
    .message("Course not found")
    .errorCode("COURSE_NOT_FOUND")
    .build();
```

---

## Adding a New Service

### Step 1: Create Module Structure
```bash
mkdir new-service
cd new-service
```

### Step 2: Create pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>com.hcmut.lms</groupId>
        <artifactId>lms-backend</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>new-service</artifactId>
    <name>New Service</name>
    
    <dependencies>
        <dependency>
            <groupId>com.hcmut.lms</groupId>
            <artifactId>common</artifactId>
        </dependency>
        <!-- Add other dependencies -->
    </dependencies>
</project>
```

### Step 3: Create Application Class
```java
package com.hcmut.lms.newservice;

@SpringBootApplication(scanBasePackages = {
    "com.hcmut.lms.newservice", 
    "com.hcmut.lms.common"
})
@EnableDiscoveryClient
public class NewServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NewServiceApplication.class, args);
    }
}
```

### Step 4: Create application.yml
```yaml
server:
  port: 8093

spring:
  application:
    name: new-service
  datasource:
    url: jdbc:h2:mem:newservicedb
    driver-class-name: org.h2.Driver
    username: sa
    password: 

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### Step 5: Update Root pom.xml
Add module to root pom.xml:
```xml
<modules>
    <!-- Existing modules -->
    <module>new-service</module>
</modules>
```

### Step 6: Update API Gateway
Add routes to api-gateway/src/main/resources/application.yml:
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: new-service
          uri: lb://new-service
          predicates:
            - Path=/api/new-service/**
```

---

## Adding a New Feature

### Step 1: Create Entity
```java
@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

### Step 2: Create Repository
```java
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByNameContaining(String name);
}
```

### Step 3: Create DTO
```java
@Data
@Builder
public class CourseDto {
    private Long id;
    private String name;
    private String description;
}
```

### Step 4: Create Service
```java
@Service
@Transactional
public class CourseService {
    
    @Autowired
    private CourseRepository courseRepository;
    
    public CourseDto createCourse(CourseDto dto) {
        Course course = Course.builder()
            .name(dto.getName())
            .description(dto.getDescription())
            .build();
            
        Course saved = courseRepository.save(course);
        return mapToDto(saved);
    }
    
    private CourseDto mapToDto(Course course) {
        return CourseDto.builder()
            .id(course.getId())
            .name(course.getName())
            .description(course.getDescription())
            .build();
    }
}
```

### Step 5: Create Controller
```java
@RestController
@RequestMapping("/api/courses")
public class CourseController {
    
    @Autowired
    private CourseService courseService;
    
    @PostMapping
    public ResponseDto<CourseDto> createCourse(@RequestBody CourseDto dto) {
        CourseDto created = courseService.createCourse(dto);
        return ResponseDto.<CourseDto>builder()
            .success(true)
            .message("Course created successfully")
            .data(created)
            .build();
    }
}
```

---

## Testing Guidelines

### Unit Tests
```java
@SpringBootTest
class CourseServiceTest {
    
    @MockBean
    private CourseRepository courseRepository;
    
    @Autowired
    private CourseService courseService;
    
    @Test
    void testCreateCourse() {
        // Given
        CourseDto dto = CourseDto.builder()
            .name("Test Course")
            .build();
            
        // When
        CourseDto created = courseService.createCourse(dto);
        
        // Then
        assertNotNull(created);
        assertEquals("Test Course", created.getName());
    }
}
```

### Integration Tests
```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CourseControllerTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testCreateCourse() {
        CourseDto dto = CourseDto.builder()
            .name("Test Course")
            .build();
            
        ResponseEntity<ResponseDto> response = restTemplate.postForEntity(
            "/api/courses", 
            dto, 
            ResponseDto.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isSuccess());
    }
}
```

### Run Tests
```bash
# Run all tests (Linux/Mac)
./mvnw test

# Windows
mvnw.cmd test

# Run tests for specific service
cd course-management-service
../mvnw test    # Linux/Mac
..\mvnw.cmd test    # Windows

# Run with coverage
./mvnw clean verify    # Linux/Mac
mvnw.cmd clean verify  # Windows
```

---

## Database Migration

### Using Flyway (Recommended)
```sql
-- V1__Create_courses_table.sql
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## API Documentation

### Swagger/OpenAPI
Add dependency to pom.xml:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.2.0</version>
</dependency>
```

Access: `http://localhost:8080/swagger-ui.html`

---

## Troubleshooting

### Common Issues

**Issue 1: Port Already in Use**
```bash
# Find process using port
lsof -ti:8081

# Kill process
kill -9 <PID>
```

**Issue 2: Service Not Registering with Eureka**
- Check Eureka Server is running
- Verify eureka.client.serviceUrl.defaultZone
- Check network connectivity

**Issue 3: Build Failure**
```bash
# Clean and rebuild
mvn clean install -U
```

**Issue 4: H2 Console Not Accessible**
```yaml
spring:
  h2:
    console:
      enabled: true
      path: /h2-console
```

---

## Best Practices

1. **Always use DTOs** - Never expose entities directly
2. **Validate input** - Use @Valid and validation annotations
3. **Handle exceptions** - Use @ControllerAdvice
4. **Log appropriately** - Use SLF4J with proper log levels
5. **Write tests** - Aim for >80% coverage
6. **Document APIs** - Use Swagger annotations
7. **Version APIs** - Use /api/v1 prefix
8. **Use transactions** - @Transactional where needed
9. **Secure endpoints** - Implement proper authentication
10. **Monitor performance** - Use Actuator metrics

---

## Git Workflow

```bash
# Create feature branch
git checkout -b feature/new-feature

# Make changes and commit
git add .
git commit -m "feat: add new feature"

# Push to remote
git push origin feature/new-feature

# Create Pull Request on GitHub
```

### Commit Message Convention
```
feat: add new feature
fix: bug fix
docs: documentation update
refactor: code refactoring
test: add tests
chore: maintenance tasks
```

