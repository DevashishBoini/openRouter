package gateway.filter;

import gateway.dbModel.ApiKey;
import gateway.exception.InvalidApiKeyException;
import gateway.security.ApiKeyAuthentication;
import gateway.service.ApiKeyValidationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that extracts and validates API keys from the Authorization header.
 *
 * <p>Expects header format: Authorization: Bearer sk-or-v1-{UUID}</p>
 *
 * <p>If valid, sets the ApiKeyAuthentication in the SecurityContext.
 * If invalid, the request proceeds without authentication and will be
 * rejected by Spring Security.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final ApiKeyValidationService apiKeyValidationService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String apiKey = authHeader.substring(7); // Remove "Bearer " prefix

            try {
                // Validate API key
                ApiKey validatedApiKey = apiKeyValidationService.validateApiKey(apiKey);

                // Set authentication in security context
                ApiKeyAuthentication authentication = new ApiKeyAuthentication(
                        validatedApiKey,
                        validatedApiKey.getUser()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("API key authenticated: userId={}", validatedApiKey.getUser().getId());

            } catch (InvalidApiKeyException e) {
                log.warn("API key validation failed: {}", e.getMessage());
                // Don't set authentication - request will be rejected by Spring Security
            }
        }

        filterChain.doFilter(request, response);
    }
}
