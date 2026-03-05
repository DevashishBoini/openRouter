package backend.filter;

import backend.exception.InvalidCredentialsException;
import backend.security.UserPrincipal;
import backend.utils.JwtHandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHandler jwtHandler;

    public JwtAuthenticationFilter(JwtHandler jwtHandler) {
        this.jwtHandler = jwtHandler;
    }

    /**
     * Skip JWT validation for public auth routes
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.startsWith("/v1/auth/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            // If no token, just continue - Spring Security will handle authorization
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);

            // If token is invalid, just continue without setting authentication
            // Spring Security will deny access to protected routes
            if (!jwtHandler.validateJwtToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtHandler.extractEmail(token);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UserPrincipal userPrincipal = new UserPrincipal(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userPrincipal, null, null);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } finally {
            // Always clear security context to prevent thread pool pollution
            SecurityContextHolder.clearContext();
        }
    }
}