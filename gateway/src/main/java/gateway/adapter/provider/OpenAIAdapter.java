package gateway.adapter.provider;

import gateway.dto.ChatCompletionRequest;
import gateway.dto.ChatCompletionResponse;
import gateway.dto.ChatMessage;
import gateway.dto.provider.openai.OpenAIRequest;
import gateway.dto.provider.openai.OpenAIResponse;
import gateway.exception.ProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Provider adapter for OpenAI API.
 *
 * <p>Handles communication with OpenAI's chat completion API using
 * provider-specific DTOs for type safety and proper field mapping.</p>
 */
@Component
@Slf4j
public class OpenAIAdapter implements ProviderAdapter {

    private final RestTemplate restTemplate;
    private final String openaiApiKey;
    private final String openaiBaseUrl;

    public OpenAIAdapter(
            RestTemplate restTemplate,
            @Value("${provider.openai.api-key}") String openaiApiKey,
            @Value("${provider.openai.base-url}") String openaiBaseUrl) {
        this.restTemplate = restTemplate;
        this.openaiApiKey = openaiApiKey;
        this.openaiBaseUrl = openaiBaseUrl;
    }

    @Override
    public ChatCompletionResponse createChatCompletion(ChatCompletionRequest request) {

        String url = openaiBaseUrl + "/chat/completions";

        log.info("Calling OpenAI API: model={}, messages={}", request.model(), request.messages().size());

        // Strip provider prefix from model name
        String actualModelName = extractModelName(request.model());

        log.debug("Stripped model name for OpenAI: {} -> {}", request.model(), actualModelName);

        try {
            // Transform to OpenAI-specific request
            OpenAIRequest openaiRequest = buildOpenAIRequest(request, actualModelName);

            // Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + openaiApiKey);

            HttpEntity<OpenAIRequest> entity = new HttpEntity<>(openaiRequest, headers);

            // Make request with provider-specific DTO
            ResponseEntity<OpenAIResponse> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    OpenAIResponse.class
            );

            OpenAIResponse openaiResponse = responseEntity.getBody();

            if (openaiResponse == null) {
                log.error("OpenAI API returned null response");
                throw new ProviderException("OpenAI API returned empty response");
            }

            // Transform to standard response format
            ChatCompletionResponse response = transformToStandardResponse(openaiResponse, request.model());

            log.info("OpenAI API call successful: model={}, tokens={}/{}",
                    request.model(),
                    response.usage().promptTokens(),
                    response.usage().completionTokens());

            return response;

        } catch (HttpClientErrorException e) {
            log.error("OpenAI API client error: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new ProviderException("OpenAI API error: " + e.getMessage(), e);

        } catch (HttpServerErrorException e) {
            log.error("OpenAI API server error: status={}, body={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new ProviderException("OpenAI service unavailable", e);

        } catch (Exception e) {
            log.error("Unexpected error calling OpenAI API: {}", e.getMessage(), e);
            throw new ProviderException("Failed to call OpenAI API", e);
        }
    }

    @Override
    public String getProviderName() {
        return "openai";
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
     * Build OpenAI-specific request from standard request.
     */
    private OpenAIRequest buildOpenAIRequest(ChatCompletionRequest request, String modelName) {

        List<OpenAIRequest.Message> messages = request.messages().stream()
                .map(msg -> new OpenAIRequest.Message(msg.role(), msg.content(), null))
                .collect(Collectors.toList());

        return new OpenAIRequest(
                modelName,
                messages,
                request.temperature(),
                request.maxTokens(),
                request.topP(),
                request.frequencyPenalty(),
                request.presencePenalty(),
                request.stop(),
                request.stream(),
                request.user()
        );
    }

    /**
     * Transform OpenAI response to standard format.
     */
    private ChatCompletionResponse transformToStandardResponse(OpenAIResponse openaiResp, String originalModel) {

        List<ChatCompletionResponse.Choice> choices = openaiResp.choices().stream()
                .map(choice -> new ChatCompletionResponse.Choice(
                        choice.index(),
                        new ChatMessage(choice.message().role(), choice.message().content()),
                        choice.finishReason()
                ))
                .collect(Collectors.toList());

        return new ChatCompletionResponse(
                openaiResp.id(),
                openaiResp.object(),
                openaiResp.created(),
                originalModel,
                choices,
                new ChatCompletionResponse.Usage(
                        openaiResp.usage().promptTokens(),
                        openaiResp.usage().completionTokens(),
                        openaiResp.usage().totalTokens()
                )
        );
    }
}
