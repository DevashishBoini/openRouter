package backend.service;

import backend.dbModel.ApiKey;
import backend.dbModel.User;
import backend.dto.Responses.ApiKeyGetResponse;
import backend.exception.ResourceNotFoundException;
import backend.repository.ApiKeyRepository;
import backend.repository.UserRepository;
import backend.utils.ApiKeyHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyService {

   private final UserRepository userRepository;
   private final ApiKeyRepository apiKeyRepository;
   private final ApiKeyHandler apiKeyHandler;

    @Transactional
    public String createApiKey(UUID userId, String apiKeyName){

        String rawApiKeyValue = apiKeyHandler.generateApiKey(userId);
        String hashedApiKeyValue = apiKeyHandler.hashApiKey(rawApiKeyValue);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        ApiKey apiKey = ApiKey.builder()
                .name(apiKeyName)
                .apiKeyValue(hashedApiKeyValue)
                .user(user)
                .build();

        apiKeyRepository.save(apiKey);

        log.info("API key created for user {}", userId);

        return rawApiKeyValue;
    }

    private ApiKey getUserApiKey(UUID userId, UUID apiKeyId) {

        return apiKeyRepository
                .findByIdAndUserId(apiKeyId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("API key not found")
                );
    }

    public List<ApiKeyGetResponse> getApiKeys(UUID userId){

        List<ApiKey> apiKeys = apiKeyRepository.findByUserIdAndDeletedFalse(userId);
        log.info("Fetched {} API keys for user {}", apiKeys.size(), userId);

        return apiKeys.stream()
                .map(apiKey -> new ApiKeyGetResponse(
                        apiKey.getId(),
                        apiKey.getName(),
                        apiKey.isDisabled(),
                        apiKey.getCreditsConsumed(),
                        apiKey.getLastUsed(),
                        apiKey.getCreatedAt()
                ))
                .toList();

    }

    @Transactional
    public void updateApiKeyDisabledStatus(UUID userId, UUID apiKeyId, boolean disabled) {

        ApiKey apiKey = getUserApiKey(userId, apiKeyId);
        apiKey.setDisabled(disabled);
        apiKeyRepository.save(apiKey);

        log.info("API key {} updated: disabled={}", apiKeyId, disabled);
    }


    @Transactional
    public void deleteApiKey(UUID userId, UUID apiKeyId) {

        ApiKey apiKey = getUserApiKey(userId, apiKeyId);
        apiKey.setDeleted(true);
        apiKeyRepository.save(apiKey);

        log.info("API key {} deleted: ", apiKeyId);
    }




}
