package backend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class PasswordHandler {

    private final int saltRounds;

    public PasswordHandler(@Value("${passwordHandler.salt_rounds}") int saltRounds) {

        if (saltRounds < 4 || saltRounds > 31) {
            throw new IllegalStateException("Invalid BCrypt salt rounds configuration");
        }

        this.saltRounds = saltRounds;
    }

    // Hash password during signup
    public String hashPassword(String plainPassword) {

        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(saltRounds));
    }

    // Compare login password with hashed password from DB
    public boolean verifyPassword(String plainPassword, String hashedPassword) {

        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}