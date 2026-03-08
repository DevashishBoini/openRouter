package gateway.adapter.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gateway.dto.ChatCompletionRequest;
import gateway.dto.ChatCompletionResponse;
import gateway.dto.ChatMessage;
import gateway.exception.ProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Provider adapter for Google Gemini API.
 *
 * <p>Handles communication with Google's Gemini API.
 * Transforms between OpenAI-compatible format and Google's format.</p>
 *
 * <p>Key differences from OpenAI:</p>
 * <ul>
 *   <li>Uses contents array with parts structure</li>
 *   <li>API key passed as query parameter</li>
 *   <li>Different role names (user/model instead of user/assistant)</li>
 *   <li>Different request/response structure</li>
 * </ul>
 */
@Component
@Slf4j
public class GoogleAdapter implements ProviderAdapter {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String googleApiKey;
    private final String googleBaseUrl;

    public GoogleAdapter(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${provider.google.api-key}") String googleApiKey,
            @Value("${provider.google.base-url}") String googleBaseUrl) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.googleApiKey = googleApiKey;
        this.googleBaseUrl = googleBaseUrl;

        log.info(googleApiKey);
    }

    @Override
    public ChatCompletionResponse createChatCompletion(ChatCompletionRequest request) {

        String actualModelName = extractModelName(request.model());

        String url = googleBaseUrl + "/models/" + actualModelName + ":generateContent?key=" + googleApiKey;

        log.info("Calling Google Gemini API: model={}, messages={}", request.model(), request.messages().size());

        log.debug("Stripped model name for Google: {} -> {}", request.model(), actualModelName);

        try {
            // Transform OpenAI format to Google format
            Map<String, Object> googleRequest = buildGoogleRequest(request);

            // Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(googleRequest, headers);

            // Make request
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String responseBody = responseEntity.getBody();

            if (responseBody == null) {
                log.error("Google API returned null response");
                throw new ProviderException("Google API returned empty response");
            }

            // Transform Google response to OpenAI format
            ChatCompletionResponse response = transformGoogleResponse(responseBody, request.model());

            log.info("Google API call successful: model={}, tokens={}/{}",
                    request.model(),
                    response.usage().promptTokens(),
                    response.usage().completionTokens());

            return response;

        } catch (HttpClientErrorException e) {
            String errorBody = e.getResponseBodyAsString();
            log.error("Google API client error: status={}, body={}", e.getStatusCode(), errorBody);
            String errorMessage = extractGoogleErrorMessage(errorBody);
            throw new ProviderException("Google API error: " + errorMessage, e);

        } catch (HttpServerErrorException e) {
            log.error("Google API server error: status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new ProviderException("Google service unavailable", e);

        } catch (Exception e) {
            log.error("Unexpected error calling Google API: {}", e.getMessage(), e);
            throw new ProviderException("Failed to call Google API", e);
        }
    }

    @Override
    public String getProviderName() {
        return "google";
    }

    /**
     * Strip provider prefix from model name.
     */
    private String extractModelName(String modelSlug) {
        if (modelSlug.contains("/")) {
            return modelSlug.split("/", 2)[1];
        }
        return modelSlug;
    }

    /**
     * Build Google-specific request format from OpenAI format.
     */
    private Map<String, Object> buildGoogleRequest(ChatCompletionRequest request) {

        Map<String, Object> googleRequest = new HashMap<>();

        // Build contents array
        List<Map<String, Object>> contents = new ArrayList<>();
        String systemInstruction = null;

        for (ChatMessage msg : request.messages()) {
            if ("system".equalsIgnoreCase(msg.role())) {
                // Google handles system messages separately
                systemInstruction = msg.content();
            } else {
                Map<String, Object> content = new HashMap<>();
                // Convert role: OpenAI uses "assistant", Google uses "model"
                String role = "assistant".equalsIgnoreCase(msg.role()) ? "model" : msg.role();
                content.put("role", role);

                // Build parts array
                List<Map<String, String>> parts = new ArrayList<>();
                Map<String, String> part = new HashMap<>();
                part.put("text", msg.content());
                parts.add(part);

                content.put("parts", parts);
                contents.add(content);
            }
        }

        googleRequest.put("contents", contents);

        // Add system instruction if present
        if (systemInstruction != null) {
            Map<String, Object> systemInstructionObj = new HashMap<>();
            List<Map<String, String>> parts = new ArrayList<>();
            Map<String, String> part = new HashMap<>();
            part.put("text", systemInstruction);
            parts.add(part);
            systemInstructionObj.put("parts", parts);
            googleRequest.put("systemInstruction", systemInstructionObj);
        }

        // Build generation config
        Map<String, Object> generationConfig = new HashMap<>();

        if (request.temperature() != null) {
            generationConfig.put("temperature", request.temperature());
        }

        if (request.topP() != null) {
            generationConfig.put("topP", request.topP());
        }

        if (request.maxTokens() != null) {
            generationConfig.put("maxOutputTokens", request.maxTokens());
        }

        if (request.stop() != null && !request.stop().isEmpty()) {
            generationConfig.put("stopSequences", request.stop());
        }

        if (!generationConfig.isEmpty()) {
            googleRequest.put("generationConfig", generationConfig);
        }

        return googleRequest;
    }

    /**
     * Transform Google response to OpenAI-compatible format.
     */
    private ChatCompletionResponse transformGoogleResponse(String responseBody, String originalModel) {
        try {
            JsonNode googleResponse = objectMapper.readTree(responseBody);

            // Extract text content from candidates array
            String content = "";
            String finishReason = "stop";

            JsonNode candidates = googleResponse.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode candidate = candidates.get(0);

                // Extract content
                JsonNode contentNode = candidate.get("content");
                if (contentNode != null) {
                    JsonNode parts = contentNode.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        content = parts.get(0).get("text").asText();
                    }
                }

                // Extract finish reason
                if (candidate.has("finishReason")) {
                    finishReason = mapFinishReason(candidate.get("finishReason").asText());
                }
            }

            // Extract usage information
            int promptTokens = 0;
            int completionTokens = 0;

            JsonNode usageMetadata = googleResponse.get("usageMetadata");
            if (usageMetadata != null) {
                if (usageMetadata.has("promptTokenCount")) {
                    promptTokens = usageMetadata.get("promptTokenCount").asInt();
                }
                if (usageMetadata.has("candidatesTokenCount")) {
                    completionTokens = usageMetadata.get("candidatesTokenCount").asInt();
                }
            }

            // Build OpenAI-compatible response
            return new ChatCompletionResponse(
                    "gemini-" + UUID.randomUUID().toString(),
                    "chat.completion",
                    System.currentTimeMillis() / 1000,
                    originalModel,
                    List.of(
                            new ChatCompletionResponse.Choice(
                                    0,
                                    new ChatMessage("assistant", content),
                                    finishReason
                            )
                    ),
                    new ChatCompletionResponse.Usage(
                            promptTokens,
                            completionTokens,
                            promptTokens + completionTokens
                    )
            );

        } catch (Exception e) {
            log.error("Failed to transform Google response", e);
            throw new ProviderException("Failed to parse Google response", e);
        }
    }

    /**
     * Map Google finish_reason to OpenAI format.
     */
    private String mapFinishReason(String googleFinishReason) {
        return switch (googleFinishReason) {
            case "STOP" -> "stop";
            case "MAX_TOKENS" -> "length";
            case "SAFETY" -> "content_filter";
            case "RECITATION" -> "content_filter";
            default -> "stop";
        };
    }

    /**
     * Extract error message from Google error response.
     */
    private String extractGoogleErrorMessage(String errorBody) {
        try {
            JsonNode errorJson = objectMapper.readTree(errorBody);
            JsonNode error = errorJson.get("error");
            if (error != null && error.has("message")) {
                return error.get("message").asText();
            }
        } catch (Exception e) {
            log.warn("Failed to parse Google error message", e);
        }
        return errorBody;
    }
}
