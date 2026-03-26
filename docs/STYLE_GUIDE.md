# Style Guide

> Coding standards and conventions for the GJP Admin API Spring Boot project.
> All contributors must follow these rules to ensure consistency across the codebase.

## Table of Contents

1. [Logging](#1-logging)
2. [Imports](#2-imports)
3. [Entity Classes (JPA)](#3-entity-classes-jpa)
4. [Exception Handling](#4-exception-handling)
5. [Service Layer](#5-service-layer)
6. [Controller Layer](#6-controller-layer)
7. [Javadoc](#7-javadoc)
8. [Validation](#8-validation)
9. [Naming Conventions](#9-naming-conventions)
10. [Code Formatting](#10-code-formatting)
11. [Constants and Magic Numbers](#11-constants-and-magic-numbers)

---

## 1. Logging

### Rule 1.1: All Spring-managed classes must have `@Slf4j`

Every `@Service`, `@RestController`, `@Component`, `@Configuration`, and `@ControllerAdvice` class must be annotated with `@Slf4j` from Lombok for structured logging.

```java
@Slf4j       // Always present
@Service
@RequiredArgsConstructor
public class UserService { ... }
```

> **Rationale:** Even if the class doesn't log today, adding @Slf4j upfront avoids importing a logger later and ensures every class is ready for debugging.

### Rule 1.2: Logging level guidelines

| Level | Usage |
|-------|-------|
| `log.debug()` | Internal flow tracing (token validation, cache hits, method entry/exit) |
| `log.info()` | Significant state changes (user created, role deleted, password changed) |
| `log.warn()` | Recoverable issues (account locked, rate limit hit, deprecated usage) |
| `log.error()` | Unrecoverable failures (unhandled exceptions, infrastructure errors) |

### Rule 1.3: Never log sensitive data

Never log passwords, tokens (access or refresh), secret keys, or full request bodies containing credentials.

```java
// WRONG
log.info("User logged in with token: {}", accessToken);

// CORRECT
log.info("User '{}' logged in successfully", username);
```

---

## 2. Imports

### Rule 2.1: No wildcard imports

Always use explicit imports. No `import java.util.*` or `import org.springframework.web.bind.annotation.*`.

```java
// WRONG
import java.util.*;
import org.springframework.web.bind.annotation.*;

// CORRECT
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
```

### Rule 2.2: No same-package imports

Do not import classes from the same package — Java resolves them automatically.

### Rule 2.3: Import ordering

Follow this order (alphabetical within each group), separated by blank lines:

1. Third-party libraries (`io.*`, `com.*`, etc. — e.g., `io.jsonwebtoken`)
2. `jakarta.*`
3. `lombok.*`
4. `org.ganjp.*` (project imports)
5. `org.springframework.*`
6. `java.*`

```java
import io.jsonwebtoken.Claims;              // 1. Third-party

import jakarta.servlet.http.HttpServletRequest; // 2. Jakarta

import lombok.RequiredArgsConstructor;       // 3. Lombok

import org.ganjp.api.auth.user.User;         // 4. Project
import org.ganjp.api.common.model.ApiResponse;

import org.springframework.stereotype.Service; // 5. Spring

import java.util.List;                        // 6. Java standard
```

---

## 3. Entity Classes (JPA)

### Rule 3.1: Use `@Getter` / `@Setter` instead of `@Data` on entities

JPA entities must not use `@Data` because it generates `equals()`, `hashCode()`, and `toString()` based on all fields, which causes issues with lazy-loaded relationships and Hibernate proxies.

```java
// CORRECT: Entity
@Getter
@Setter
@ToString(exclude = {"userRoles", "refreshTokens"})  // Exclude bidirectional/lazy relationships
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth_users")
public class User implements UserDetails { ... }
```

> **Rationale:** `@Data` generates `equals()`/`hashCode()` using ALL fields. When Hibernate loads a proxy, accessing uninitialized lazy fields triggers `LazyInitializationException` or N+1 queries. Using `@Getter`/`@Setter` avoids this and forces explicit, ID-based equality.

### Rule 3.2: Always exclude bidirectional relationships from `@ToString`

Use `@ToString(exclude = {...})` to prevent `LazyInitializationException` and infinite recursion.

### Rule 3.3: Implement `equals()` and `hashCode()` based on ID only

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return id != null && Objects.equals(id, user.id);
}

@Override
public int hashCode() {
    return Objects.hash(id);
}
```

> **Rationale:** ID-based equality works correctly with Hibernate proxies, detached entities, and `Set`/`Map` collections. The `id != null` guard returns `false` for transient (unsaved) entities, which is safer than using a generated hashCode.

### Rule 3.4: DTOs and configuration classes use `@Data`

DTOs (Request/Response classes) and `@ConfigurationProperties` classes should use `@Data` since they are simple POJOs without JPA proxying concerns.

```java
// CORRECT: DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse { ... }

// CORRECT: Configuration properties
@Data
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityProperties { ... }
```

---

## 4. Exception Handling

### Rule 4.1: Use project-specific exception types

| Exception | HTTP Status | Usage |
|-----------|------------|-------|
| `IllegalArgumentException` | 400 | Invalid input, business rule violation on input |
| `BusinessException` | 400 | Business logic violation in self-service operations (e.g., current password mismatch) |
| `BadCredentialsException` | 401 | Authentication failure |
| `ResourceNotFoundException` | 404 | Entity not found by ID/code |
| `DuplicateResourceException` | 409 | Unique constraint violation (use `.of()` factory method) |
| `IllegalStateException` | 409 | Operation not allowed in current state (e.g., system role modification) |

### Rule 4.2: Never use generic `RuntimeException`

Always use a specific exception type from the table above.

### Rule 4.3: Use `BusinessException` for user-facing self-service rule violations

Use `BusinessException` for user-facing self-service violations (profile updates, password changes) where the user needs actionable feedback. Use `IllegalArgumentException` for admin-facing validation in management services.

```java
// Self-service (UserProfileService)
throw new BusinessException("Current password is incorrect");

// Admin management (UserService)
throw new IllegalArgumentException("Username is already taken");
```

### Rule 4.4: Use `ResourceNotFoundException` consistently for entity lookups

```java
// CORRECT
User user = userRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

// WRONG
User user = userRepository.findById(id)
    .orElseThrow(() -> new BusinessException("User not found: " + id));
```

### Rule 4.5: Use `DuplicateResourceException.of()` factory method

```java
// CORRECT
throw DuplicateResourceException.of("Role", "code", roleCreateRequest.getCode());

// WRONG
throw new DuplicateResourceException("Role with code already exists");
```

---

## 5. Service Layer

### Rule 5.1: Constructor injection via `@RequiredArgsConstructor`

All service and configuration classes must use Lombok's `@RequiredArgsConstructor` with `private final` fields. No manual constructors for dependency injection.

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;  // Injected via constructor
}
```

> **Rationale:** Lombok generates the constructor from `final` fields, which guarantees immutability and makes it impossible to forget a dependency. Manual constructors drift out of sync when dependencies are added/removed.

### Rule 5.2: `@Transactional` on all data-modifying methods

Every service method that creates, updates, or deletes data must be annotated with `@Transactional`.

### Rule 5.3: `@Transactional(readOnly = true)` on read-only methods

Every service method that only reads data must be annotated with `@Transactional(readOnly = true)`. This applies to all `get*`, `find*`, `is*`, `count*`, and `check*` methods that issue database queries.

```java
// CORRECT
@Transactional(readOnly = true)
public UserResponse getUserById(String id) {
    return userRepository.findById(id)
            .map(this::mapToUserResponse)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
}

// WRONG — missing readOnly
public UserResponse getUserById(String id) { ... }
```

> **Rationale:** `readOnly = true` provides three benefits:
> 1. **Performance:** Hibernate skips dirty-checking on managed entities (no snapshot comparison at flush time)
> 2. **Database optimization:** Some databases (e.g., MySQL with InnoDB) can optimize read-only transactions by avoiding undo log overhead
> 3. **Intent documentation:** Makes it clear this method does not modify data, which helps reviewers and prevents accidental writes

### Rule 5.4: Use `REQUIRES_NEW` for independent side-effect recording

When a side effect (e.g., recording a failed login attempt) must persist even if the calling transaction rolls back, extract it into a separate `@Service` class and use `@Transactional(propagation = Propagation.REQUIRES_NEW)`.

```java
// LoginFailureService.java — separate bean so Spring AOP can proxy it
@Transactional(propagation = Propagation.REQUIRES_NEW)
public void recordFailedLogin(User user) {
    // This persists even if the caller's auth transaction rolls back
    userRepository.updateLoginFailureByIdNative(user.getId(), LocalDateTime.now());
}
```

> **Rationale:** `REQUIRES_NEW` opens an independent transaction. If the outer transaction (e.g., `authenticate()`) throws an exception and rolls back, the failure counter still commits. This pattern must live in a separate bean because Spring's proxy-based AOP cannot intercept `private` or `this.method()` calls within the same class.

### Rule 5.5: Consistent audit parameter naming

Service methods that need to record the acting user must use `userId` (not `username`) as the parameter name, since the value comes from the JWT token's `userId` claim.

```java
// CORRECT
public void deleteRole(String id, String userId) { ... }

// WRONG
public void deleteRole(String id, String username) { ... }
```

---

## 6. Controller Layer

### Rule 6.1: Every endpoint must have `@PreAuthorize`

No controller method should lack an authorization annotation:

```java
@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")  // Admin ops
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")                                 // Destructive ops
@PreAuthorize("isAuthenticated()")                                                 // Self-service ops
```

### Rule 6.2: Consistent response pattern

All endpoints must return `ResponseEntity<ApiResponse<T>>` using the `ApiResponse` envelope:

```java
return ResponseEntity.ok(ApiResponse.success(data, "Message"));
return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(data, "Message"));
```

### Rule 6.3: Extract `userId` from JWT consistently

Always use `jwtUtils.extractUserIdFromToken(request)` — never duplicate extraction logic.

### Rule 6.4: Avoid manual JSON construction

Never construct JSON responses via `String.format()` or string concatenation. Use `ObjectMapper` or the `ApiResponse` envelope for all JSON responses, including error handlers and filters.

```java
// WRONG
String json = String.format("{\"status\":{\"code\":401,\"message\":\"%s\"}}", errorMsg);
response.getWriter().write(json);

// CORRECT
ObjectMapper mapper = new ObjectMapper();
ApiResponse<?> apiResponse = ApiResponse.error(401, "Unauthorized", errors);
response.getWriter().write(mapper.writeValueAsString(apiResponse));
```

> **Rationale:** Manual JSON construction is fragile, error-prone (escaping issues), and violates the single-source-of-truth principle for the API response format. If the `ApiResponse` envelope changes, manual JSON strings silently diverge.

> **Exception:** Servlet filters (e.g., `LoginRateLimitFilter`, `AuthenticationEntryPoint`) run outside Spring MVC and cannot inject beans easily. In these cases, inject a shared `ObjectMapper` bean or use a static helper. If that is not feasible, document the manual JSON as a known deviation with a `// TODO: migrate to ObjectMapper` comment.

---

## 7. Javadoc

### Rule 7.1: All public methods must have Javadoc

Every public method in controllers, services, and utility classes must have a Javadoc comment with at minimum a one-line description.

```java
/**
 * Get all roles sorted by sort order and name.
 *
 * @return list of all roles
 */
public List<RoleResponse> getAllRoles() { ... }
```

### Rule 7.2: Controller Javadoc should describe the endpoint

```java
/**
 * Retrieve all roles in the system.
 *
 * @return list of roles sorted by sort order
 */
@PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_SUPER_ADMIN')")
@GetMapping
public ResponseEntity<ApiResponse<List<RoleResponse>>> getAllRoles() { ... }
```

### Rule 7.3: Entity fields should have Javadoc for non-obvious fields

Standard audit fields (`createdAt`, `updatedAt`) do not need Javadoc. Domain-specific fields should.

---

## 8. Validation

### Rule 8.1: Unified password policy across all DTOs

All DTOs accepting passwords must use the same constraints:

```java
@Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^+=])(?=\\S+$).+$",
         message = "Password must contain at least one uppercase, one lowercase, one digit, one special character, and no whitespace")
```

### Rule 8.2: Use Bean Validation on DTOs, not in service methods

Prefer declarative validation on DTOs; service methods should focus on business rules (uniqueness, authorization).

---

## 9. Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Entity class | Singular noun | `User`, `Role` |
| DTO class | Entity + purpose + suffix | `UserCreateRequest`, `UserResponse` |
| Service class | Entity + `Service` | `UserService`, `AuthService` |
| Controller class | Entity + `Controller` | `UserController` |
| Repository | Entity + `Repository` | `UserRepository` |
| Service methods | `get*`, `find*`, `create*`, `update*`, `delete*`, `toggle*` | `getUserById()` |
| Boolean getters | `is*`, `has*` | `isActive()`, `hasChildren()` |
| Private mappers | `mapTo*Response` or `buildTo*Response` | `mapToRoleResponse()` |

---

## 10. Code Formatting

### Rule 10.1: Class-level annotation ordering

```java
@Slf4j                    // 1. Lombok utility
@Getter @Setter           // 2. Lombok data (or @Data for DTOs)
@Builder                  // 3. Lombok builder
@NoArgsConstructor        // 4. Lombok constructors
@AllArgsConstructor
@Entity / @Service / ...  // 5. Spring/JPA annotations
@Table(...)               // 6. JPA mapping
@RequiredArgsConstructor  // 7. Spring DI (for services/controllers)
public class Foo { ... }
```

### Rule 10.2: Method ordering in classes

1. Static fields / constants
2. Instance fields (injected dependencies)
3. Public methods (CRUD order: create, read, update, delete)
4. Private helper methods

### Rule 10.3: Blank lines

- One blank line between methods
- One blank line between logical sections within a method
- No trailing blank lines at end of file

---

## 11. Constants and Magic Numbers

### Rule 11.1: Extract magic numbers into named constants

All numeric literals with domain meaning must be extracted into `private static final` constants with descriptive names.

```java
// CORRECT
private static final int MAX_FAILED_ATTEMPTS = 5;
private static final int LOCK_DURATION_MINUTES = 30;

// WRONG — inline magic number
if (user.getFailedLoginAttempts() >= 5) { ... }
```

### Rule 11.2: `@Scheduled` rate/delay values should reference constants or config

```java
// ACCEPTABLE — clearly named constant
private static final long CLEANUP_INTERVAL_MS = 1_800_000; // 30 minutes
@Scheduled(fixedRate = CLEANUP_INTERVAL_MS)

// PREFERRED — externalized to application.yml
@Scheduled(fixedRateString = "${cleanup.interval-ms:1800000}")
```

> **Rationale:** Named constants make the intent clear ("what does 1800000 mean?") and allow changing values without reading the code. Config-based values allow environment-specific tuning without recompilation.
