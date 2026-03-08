package gateway.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyHandler {

    private final int saltRounds;

    public ApiKeyHandler(@Value("${apiKeyHandler.salt_rounds}") int saltRounds) {

        if (saltRounds < 4 || saltRounds > 31) {
            throw new IllegalStateException("Invalid BCrypt salt rounds configuration");
        }

        this.saltRounds = saltRounds;
    }

    public boolean verifyApiKey(String plainApiKey, String hashedApiKey) {
        return BCrypt.checkpw(plainApiKey, hashedApiKey);
    }
}
