package gateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gateway.dbModel.ApiKey;
import gateway.dbModel.Conversation;
import gateway.dbModel.ModelProviderMapping;
import gateway.dbModel.User;
import gateway.dto.ChatCompletionRequest;
import gateway.dto.ChatCompletionResponse;
import gateway.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for tracking and saving conversation data.
 *
 * <p>Records each LLM interaction including request, response, token usage,
 * cost, and performance metrics to the conversations table.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Saves a conversation record to the database.
     *
     * @param request the original chat completion request
     * @param response the LLM provider response
     * @param user the user who made the request
     * @param apiKey the API key used for the request
     * @param mapping the model-provider mapping used
     * @param creditsCost the calculated cost in credits
     * @param responseTimeMs the time taken for the LLM call in milliseconds
     */
    @Transactional
    public void saveConversation(
            ChatCompletionRequest request,
            ChatCompletionResponse response,
            User user,
            ApiKey apiKey,
            ModelProviderMapping mapping,
            double creditsCost,
            Integer responseTimeMs) {

        try {
            // Convert request messages to JSON string
            String inputJson = objectMapper.writeValueAsString(request.messages());

            // Extract response content
            String outputContent = response.choices().get(0).message().content();

            // Build conversation entity
            Conversation conversation = Conversation.builder()
                    .input(inputJson)
                    .inputTokenCount(response.usage().promptTokens())
                    .output(outputContent)
                    .outputTokenCount(response.usage().completionTokens())
                    .creditsCost(creditsCost)
                    .responseTimeMs(responseTimeMs)
                    .user(user)
                    .apiKey(apiKey)
                    .modelProviderMapping(mapping)
                    .build();

            conversationRepository.save(conversation);

            log.info("Conversation saved: userId={}, apiKeyId={}, tokens={}/{}, cost={}, responseTime={}ms",
                    user.getId(),
                    apiKey.getId(),
                    response.usage().promptTokens(),
                    response.usage().completionTokens(),
                    creditsCost,
                    responseTimeMs);

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize conversation input: {}", e.getMessage(), e);
            // Don't throw - we don't want to fail the request if logging fails
        }
    }
}
