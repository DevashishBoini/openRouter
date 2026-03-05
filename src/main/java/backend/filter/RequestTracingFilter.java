package backend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that adds correlation ID to each request for distributed tracing.
 * The correlation ID is:
 * - Generated if not provided in X-Correlation-ID header
 * - Added to MDC (Mapped Diagnostic Context) for logging
 * - Returned in response headers
 */
@Component
@Order(1)
public class RequestTracingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestTracingFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // Get or generate correlation ID
        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Add correlation ID to MDC for logging
        MDC.put(CORRELATION_ID_KEY, correlationId);

        // Add correlation ID to response header
        httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);

        long startTime = System.currentTimeMillis();

        try {
            // Log incoming request
            logger.info("Incoming request: method={}, uri={}, remoteAddr={}",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    getClientIpAddress(httpRequest));

            chain.doFilter(request, response);

        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // Log outgoing response
            logger.info("Outgoing response: method={}, uri={}, status={}, duration={}ms",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    httpResponse.getStatus(),
                    duration);

            // Clear MDC to prevent memory leaks
            MDC.clear();
        }
    }

    /**
     * Get the client's IP address, checking for proxy headers first.
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
