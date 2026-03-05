package backend.security;

import backend.dto.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom authentication entry point that returns JSON error responses
 * instead of redirecting to login page or showing default error page.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        logger.warn("Unauthorized access attempt: uri={}, error={}",
                request.getRequestURI(),
                authException.getMessage());

        // Set response status and content type
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Create error response
        ErrorResponse errorResponse = new ErrorResponse(
                false,
                "Authentication required. Please provide a valid JWT token.",
                null
        );

        // Write JSON response
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
