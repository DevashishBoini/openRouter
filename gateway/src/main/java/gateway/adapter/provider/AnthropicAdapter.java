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
 * Provider adapter for Anthropic API (Claude models).
 *
 * <p>Handles communication with Anthropic's Messages API.
 * Transforms between OpenAI-compatible format and Anthropic's format.</p>
 *
 * <p>Key differences from OpenAI:</p>
 * <ul>
 *   <li>System messages extracted and sent separately</li>
 *   <li>Uses x-api-key header instead of Authorization Bearer</li>
 *   <li>Requires anthropic-version header</li>
 *   <li>Different request/response structure</li>
 * </ul>
 */
@Component
@Slf4j
public class AnthropicAdapter implements ProviderAdapter {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String anthropicApiKey;
    private final String anthropicBaseUrl;
    private final String anthropicVersion;

    public AnthropicAdapter(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${provider.anthropic.api-key}") String anthropicApiKey,
            @Value("${provider.anthropic.base-url}") String anthropicBaseUrl,
            @Value("${provider.anthropic.version}") String anthropicVersion) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.anthropicApiKey = anthropicApiKey;
        this.anthropicBaseUrl = anthropicBaseUrl;
        this.anthropicVersion = anthropicVersion;
    }

    @Override
    public ChatCompletionResponse createChatCompletion(ChatCompletionRequest request) {

        String url = anthropicBaseUrl + "/messages";

        log.info("Calling Anthropic API: model={}, messages={}", request.model(), request.messages().size());

        // Strip provider prefix from model name
        String actualModelName = extractModelName(request.model());

        log.debug("Stripped model name for Anthropic: {} -> {}", request.model(), actualModelName);

        try {
            // Transform OpenAI format to Anthropic format
            Map<String, Object> anthropicRequest = buildAnthropicRequest(request, actualModelName);

            // Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", anthropicApiKey);
            headers.set("anthropic-version", anthropicVersion);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(anthropicRequest, headers);

            // Make request
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String responseBody = responseEntity.getBody();

            if (responseBody == null) {
                log.error("Anthropic API returned null response");
                throw new ProviderException("Anthropic API returned empty response");
            }

            // Transform Anthropic response to OpenAI format
            ChatCompletionResponse response = transformAnthropicResponse(responseBody, request.model());

            log.info("Anthropic API call successful: model={}, tokens={}/{}",
                    request.model(),
                    response.usage().promptTokens(),
                    response.usage().completionTokens());

            return response;

        } catch (HttpClientErrorException e) {
            String errorBody = e.getResponseBodyAsString();
            log.error("Anthropic API client error: status={}, body={}", e.getStatusCode(), errorBody);
            String errorMessage = extractAnthropicErrorMessage(errorBody);
            throw new ProviderException("Anthropic API error: " + errorMessage, e);

        } catch (HttpServerErrorException e) {
            log.error("Anthropic API server error: status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new ProviderException("Anthropic service unavailable", e);

        } catch (Exception e) {
            log.error("Unexpected error calling Anthropic API: {}", e.getMessage(), e);
            throw new ProviderException("Failed to call Anthropic API", e);
        }
    }

    @Override
    public String getProviderName() {
        return "anthropic";
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
     * Build Anthropic-specific request format from OpenAI format.
     */
    private Map<String, Object> buildAnthropicRequest(ChatCompletionRequest request, String modelName) {

        Map<String, Object> anthropicRequest = new HashMap<>();

        anthropicRequest.put("model", modelName);

        // Extract system message (Anthropic handles it separately)
        String systemMessage = null;
        List<Map<String, String>> messages = new ArrayList<>();

        for (ChatMessage msg : request.messages()) {
            if ("system".equalsIgnoreCase(msg.role())) {
                systemMessage = msg.content();
            } else {
                Map<String, String> message = new HashMap<>();
                message.put("role", msg.role());
                message.put("content", msg.content());
                messages.add(message);
            }
        }

        if (systemMessage != null) {
            anthropicRequest.put("system", systemMessage);
        }

        anthropicRequest.put("messages", messages);

        // Add max_tokens (required for Anthropic)
        int maxTokens = request.maxTokens() != null ? request.maxTokens() : 4096;
        anthropicRequest.put("max_tokens", maxTokens);

        // Add optional parameters
        if (request.temperature() != null) {
            anthropicRequest.put("temperature", request.temperature());
        }

        if (request.topP() != null) {
            anthropicRequest.put("top_p", request.topP());
        }

        if (request.stop() != null && !request.stop().isEmpty()) {
            anthropicRequest.put("stop_sequences", request.stop());
        }

        return anthropicRequest;
    }

    /**
     * Transform Anthropic response to OpenAI-compatible format.
     */
    private ChatCompletionResponse transformAnthropicResponse(String responseBody, String originalModel) {
        try {
            JsonNode anthropicResponse = objectMapper.readTree(responseBody);

            // Extract text content from content array
            String content = "";
            JsonNode contentArray = anthropicResponse.get("content");
            if (contentArray != null && contentArray.isArray() && contentArray.size() > 0) {
                content = contentArray.get(0).get("text").asText();
            }

            // Extract usage information
            JsonNode usage = anthropicResponse.get("usage");
            int promptTokens = usage.get("input_tokens").asInt();
            int completionTokens = usage.get("output_tokens").asInt();

            // Determine finish reason
            String stopReason = anthropicResponse.get("stop_reason").asText();
            String finishReason = mapStopReason(stopReason);

            // Build OpenAI-compatible response
            return new ChatCompletionResponse(
                    anthropicResponse.get("id").asText(),
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
            log.error("Failed to transform Anthropic response", e);
            throw new ProviderException("Failed to parse Anthropic response", e);
        }
    }

    /**
     * Map Anthropic stop_reason to OpenAI finish_reason.
     */
    private String mapStopReason(String stopReason) {
        return switch (stopReason) {
            case "end_turn" -> "stop";
            case "max_tokens" -> "length";
            case "stop_sequence" -> "stop";
            default -> stopReason;
        };
    }

    /**
     * Extract error message from Anthropic error response.
     */
    private String extractAnthropicErrorMessage(String errorBody) {
        try {
            JsonNode errorJson = objectMapper.readTree(errorBody);
            JsonNode error = errorJson.get("error");
            if (error != null && error.has("message")) {
                return error.get("message").asText();
            }
        } catch (Exception e) {
            log.warn("Failed to parse Anthropic error message", e);
        }
        return errorBody;
    }
}
