package gateway.service;

import gateway.dbModel.ApiKey;
import gateway.exception.InvalidApiKeyException;
import gateway.repository.ApiKeyRepository;
import gateway.utils.ApiKeyHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service responsible for validating API keys from incoming requests.
 *
 * <p>Validates that the provided API key is active, not disabled, not deleted,
 * and matches the hashed value stored in the database using BCrypt.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyValidationService {

    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyHandler apiKeyHandler;

    /**
     * Validates the provided API key and returns the associated ApiKey entity.
     *
     * @param rawApiKey the plain-text API key from the Authorization header
     * @return the validated ApiKey entity
     * @throws InvalidApiKeyException if the API key is invalid, disabled, or deleted
     */
    public ApiKey validateApiKey(String rawApiKey) {

        if (rawApiKey == null || rawApiKey.isBlank()) {
            log.warn("API key validation failed: API key is null or empty");
            throw new InvalidApiKeyException("API key is required");
        }

        // Get all active API keys
        List<ApiKey> activeApiKeys = apiKeyRepository.findAllActiveApiKeys();

        if (activeApiKeys.isEmpty()) {
            log.warn("API key validation failed: No active API keys found in database");
            throw new InvalidApiKeyException("Invalid API key");
        }

        // Check each active API key using BCrypt
        for (ApiKey apiKey : activeApiKeys) {
            if (apiKeyHandler.verifyApiKey(rawApiKey, apiKey.getApiKeyValue())) {
                log.info("API key validated successfully: apiKeyId={}, userId={}",
                        apiKey.getId(), apiKey.getUser().getId());
                return apiKey;
            }
        }

        log.warn("API key validation failed: No matching API key found");
        throw new InvalidApiKeyException("Invalid API key");
    }
}
