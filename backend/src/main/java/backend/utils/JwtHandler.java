package backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class JwtHandler {

    private final Key secretKey;
    private final long expirationTime;

    public JwtHandler(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationTime
    ) {

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured");
        }

        if (expirationTime <= 0) {
            throw new IllegalStateException("JWT expiration time is not configured properly");
        }

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationTime = expirationTime;
    }

    public String generateJwtToken(UUID userId, String email){

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public boolean validateJwtToken(String token) {

        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    public String extractEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }
}
