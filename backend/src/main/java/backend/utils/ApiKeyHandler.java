package backend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ApiKeyHandler {
    private final int saltRounds;

    public ApiKeyHandler(@Value("${apiKeyHandler.salt_rounds}") int saltRounds) {

        if (saltRounds < 4 || saltRounds > 31) {
            throw new IllegalStateException("Invalid BCrypt salt rounds configuration");
        }

        this.saltRounds = saltRounds;
    }

    public String generateApiKey(UUID userId){

        return "sk-or-v1-" + UUID.randomUUID();
    }

    // Hash api key during creation and save
    public String hashApiKey(String plainApiKey) {

        if (plainApiKey == null || plainApiKey.isBlank()) {
            throw new IllegalArgumentException("ApiKey cannot be null or empty");
        }

        return BCrypt.hashpw(plainApiKey, BCrypt.gensalt(saltRounds));
    }

    // Compare user-provided api key with hashed api key from DB
    public boolean verifyApiKey(String plainApiKey, String hashedApiKey) {

        return BCrypt.checkpw(plainApiKey, hashedApiKey);
    }



}
