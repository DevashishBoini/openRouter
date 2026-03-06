package backend.service;

import backend.dbModel.ApiKey;
import backend.dbModel.User;
import backend.exception.ResourceNotFoundException;
import backend.repository.ApiKeyRepository;
import backend.repository.UserRepository;
import backend.utils.ApiKeyHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
