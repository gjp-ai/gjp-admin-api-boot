package org.ganjp.api.common.audit;

import org.ganjp.api.auth.security.JwtUtils;
import org.ganjp.api.common.util.IpAddressUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for creating audit log entries
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private JwtUtils jwtUtils;

    // -----------------------------------------------------------------------
    // Synchronous helper – call on the request thread BEFORE dispatching async
    // -----------------------------------------------------------------------

    /**
     * Capture all volatile request data synchronously while the
     * {@link HttpServletRequest} is still valid (i.e. on the request thread).
     * Pass the returned {@link RequestData} to the async audit methods.
     */
    public RequestData extractRequestData(HttpServletRequest request) {
        if (request == null) return new RequestData(null, null, null, null, null, null);
        try {
            return new RequestData(
                extractUserIdFromRequest(request),
                extractUsernameFromRequest(request),
                getRequestId(request),
                IpAddressUtils.getClientIp(request),
                request.getHeader("User-Agent"),
                safeSessionId(request)
            );
        } catch (Exception e) {
            log.warn("Could not extract request data: {}", e.getMessage());
            return new RequestData(null, null, null, null, null, null);
        }
    }

    /** Immutable snapshot of per-request metadata captured on the request thread. */
    public static final class RequestData {
        public final String userId;
        public final String username;
        public final String requestId;
        public final String ipAddress;
        public final String userAgent;
        public final String sessionId;

        public RequestData(String userId, String username, String requestId,
                           String ipAddress, String userAgent, String sessionId) {
            this.userId    = userId;
            this.username  = username;
            this.requestId = requestId;
            this.ipAddress = ipAddress;
            this.userAgent = userAgent;
            this.sessionId = sessionId;
        }
    }

    // -----------------------------------------------------------------------
    // Async logging methods – accept pre-extracted RequestData, no HttpServletRequest
    // -----------------------------------------------------------------------

    /**
     * Log a successful operation.
     * Call {@link #extractRequestData(HttpServletRequest)} synchronously on the
     * request thread and pass the result here.
     */
    @Async
    public void logSuccess(
            String httpMethod,
            String endpoint,
            String resultMessage,
            Integer statusCode,
            RequestData requestData,
            Long durationMs) {

        try {
            AuditLog auditLog = AuditLog.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(requestData.userId)
                    .username(requestData.username)
                    .httpMethod(httpMethod)
                    .endpoint(endpoint)
                    .result(truncateResult(resultMessage))
                    .statusCode(statusCode)
                    .ipAddress(requestData.ipAddress)
                    .userAgent(requestData.userAgent)
                    .sessionId(requestData.sessionId)
                    .requestId(requestData.requestId)
                    .durationMs(durationMs)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log created: {} - {} - {}", httpMethod, endpoint, resultMessage);

        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }

    /**
     * Log a failed operation.
     * Call {@link #extractRequestData(HttpServletRequest)} synchronously on the
     * request thread and pass the result here.
     */
    @Async
    public void logFailure(
            String httpMethod,
            String endpoint,
            String resultMessage,
            Integer statusCode,
            String errorMessage,
            RequestData requestData,
            Long durationMs) {

        try {
            AuditLog auditLog = AuditLog.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(requestData.userId)
                    .username(requestData.username)
                    .httpMethod(httpMethod)
                    .endpoint(endpoint)
                    .result(truncateResult(resultMessage))
                    .statusCode(statusCode)
                    .errorMessage(errorMessage)
                    .ipAddress(requestData.ipAddress)
                    .userAgent(requestData.userAgent)
                    .sessionId(requestData.sessionId)
                    .requestId(requestData.requestId)
                    .durationMs(durationMs)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Audit log created: {} - {} - {}", httpMethod, endpoint, resultMessage);

        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }

    /**
     * Log authentication events with detailed data.
     * All request-derived values must be extracted synchronously before calling;
     * pass ipAddress, userAgent, sessionId, requestId as plain Strings.
     *
     * @deprecated Prefer {@link #logAuthenticationEvent(AuthenticationAuditData)}.
     */
    @Async
    public void logAuthenticationEventWithData(
            String httpMethod,
            String endpoint,
            String userId,
            String username,
            String resultMessage,
            Integer statusCode,
            String ipAddress,
            String userAgent,
            String sessionId,
            String requestId,
            Long durationMs) {

        try {
            if ("Login successful".equals(resultMessage)) {
                log.info("User {} successfully logged in from IP: {}", username, ipAddress);
            } else if ("Logout successful".equals(resultMessage)) {
                log.info("User {} logged out from IP: {}", username, ipAddress);
            }

            AuditLog auditLog = AuditLog.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .username(username)
                    .httpMethod(httpMethod)
                    .endpoint(endpoint)
                    .result(truncateResult(resultMessage))
                    .statusCode(statusCode)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .sessionId(sessionId)
                    .requestId(requestId)
                    .durationMs(durationMs)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Authentication audit log created: {} - {} - {}", httpMethod, endpoint, resultMessage);

        } catch (Exception e) {
            log.error("Failed to create authentication audit log", e);
        }
    }

    /**
     * Log authentication events with extracted request data (for async processing)
     */
    @Async
    public void logAuthenticationEvent(AuthenticationAuditData auditData) {
        try {
            if ("Login successful".equals(auditData.resultMessage)) {
                log.info("User {} successfully logged in from IP: {}", auditData.username, auditData.ipAddress);
            } else if ("Logout successful".equals(auditData.resultMessage)) {
                log.info("User {} logged out from IP: {}", auditData.username, auditData.ipAddress);
            }

            AuditLog auditLog = AuditLog.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(auditData.userId)
                    .username(auditData.username)
                    .httpMethod(auditData.httpMethod)
                    .endpoint(auditData.endpoint)
                    .result(truncateResult(auditData.resultMessage))
                    .statusCode(auditData.statusCode)
                    .ipAddress(auditData.ipAddress)
                    .userAgent(auditData.userAgent)
                    .sessionId(auditData.sessionId)
                    .requestId(auditData.requestId)
                    .durationMs(auditData.durationMs)
                    .timestamp(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.debug("Authentication audit log created: {} - {} - {}", auditData.httpMethod, auditData.endpoint, auditData.resultMessage);

        } catch (Exception e) {
            log.error("Failed to create authentication audit log", e);
        }
    }

    /**
     * Data class for authentication audit information
     */
    public static class AuthenticationAuditData {
        public final String httpMethod;
        public final String endpoint;
        public final String userId;
        public final String username;
        public final String resultMessage;
        public final Integer statusCode;
        public final String ipAddress;
        public final String userAgent;
        public final String sessionId;
        public final String requestId;
        public final Long durationMs;

        private AuthenticationAuditData(Builder builder) {
            this.httpMethod = builder.httpMethod;
            this.endpoint = builder.endpoint;
            this.userId = builder.userId;
            this.username = builder.username;
            this.resultMessage = builder.resultMessage;
            this.statusCode = builder.statusCode;
            this.ipAddress = builder.ipAddress;
            this.userAgent = builder.userAgent;
            this.sessionId = builder.sessionId;
            this.requestId = builder.requestId;
            this.durationMs = builder.durationMs;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String httpMethod;
            private String endpoint;
            private String userId;
            private String username;
            private String resultMessage;
            private Integer statusCode;
            private String ipAddress;
            private String userAgent;
            private String sessionId;
            private String requestId;
            private Long durationMs;

            public Builder httpMethod(String httpMethod) { this.httpMethod = httpMethod; return this; }
            public Builder endpoint(String endpoint) { this.endpoint = endpoint; return this; }
            public Builder userId(String userId) { this.userId = userId; return this; }
            public Builder username(String username) { this.username = username; return this; }
            public Builder resultMessage(String resultMessage) { this.resultMessage = resultMessage; return this; }
            public Builder statusCode(Integer statusCode) { this.statusCode = statusCode; return this; }
            public Builder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
            public Builder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
            public Builder sessionId(String sessionId) { this.sessionId = sessionId; return this; }
            public Builder requestId(String requestId) { this.requestId = requestId; return this; }
            public Builder durationMs(Long durationMs) { this.durationMs = durationMs; return this; }

            public AuthenticationAuditData build() {
                return new AuthenticationAuditData(this);
            }
        }
    }

    // Helper methods

    private static final int MAX_RESULT_LENGTH = 500;

    private String truncateResult(String result) {
        if (result == null) return "UNKNOWN";
        return result.length() > MAX_RESULT_LENGTH
                ? result.substring(0, MAX_RESULT_LENGTH - 3) + "..."
                : result;
    }

    /**
     * Get request ID from various sources
     */
    private String getRequestId(HttpServletRequest request) {
        // Try to get from request attribute first
        String requestId = (String) request.getAttribute("REQUEST_ID");
        if (requestId != null) {
            return requestId;
        }

        // Try to get from MDC
        requestId = org.slf4j.MDC.get("requestId");
        if (requestId != null) {
            return requestId;
        }

        // Generate a new one if none found
        return UUID.randomUUID().toString();
    }

    /**
     * Extract user ID directly from HTTP request JWT token
     * This method is used for sync extraction before async processing
     */
    private String extractUserIdFromRequest(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String userId = jwtUtils.extractUserId(token);
                if (userId != null && !userId.trim().isEmpty()) {
                    return userId;
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract user ID from JWT token in request: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Extract username directly from HTTP request JWT token
     * This method is used for sync extraction before async processing
     */
    private String extractUsernameFromRequest(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                String username = jwtUtils.extractUsername(token);
                if (username != null && !username.trim().isEmpty()) {
                    return username;
                }
            }
        } catch (Exception e) {
            log.debug("Could not extract username from JWT token in request: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Get client IP address from HTTP request
     */
    private String getClientIpAddress(HttpServletRequest request) {
        return IpAddressUtils.getClientIp(request);
    }

    /**
     * Get user agent from HTTP request
     */
    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * Get session ID from HTTP request safely (the session may not exist).
     */
    private String getSessionId(HttpServletRequest request) {
        return safeSessionId(request);
    }

    private String safeSessionId(HttpServletRequest request) {
        try {
            return request.getSession(false) != null ? request.getSession().getId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Clean up old audit logs based on retention policy
     */
    public void cleanupOldAuditLogs(int retentionDays) {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(retentionDays);
            log.info("Cleaning up audit logs older than {} days (before {})", retentionDays, cutoffDate);
            
            // Perform actual cleanup using repository
            auditLogRepository.deleteOldAuditLogs(cutoffDate);
            log.info("Cleanup completed for audit logs older than {}", cutoffDate);
        } catch (Exception e) {
            log.error("Failed to cleanup old audit logs", e);
            throw e;
        }
    }
}
