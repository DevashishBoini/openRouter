package gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Standard chat completion response DTO.
 *
 * <p>This is the normalized response format returned to clients.
 * Provider-specific responses are transformed into this format.
 * Unknown fields are ignored to handle API evolution.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatCompletionResponse(
        String id,

        String object,

        Long created,

        String model,

        List<Choice> choices,

        Usage usage
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Choice(
            Integer index,

            ChatMessage message,

            @JsonProperty("finish_reason")
            String finishReason
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(
            @JsonProperty("prompt_tokens")
            Integer promptTokens,

            @JsonProperty("completion_tokens")
            Integer completionTokens,

            @JsonProperty("total_tokens")
            Integer totalTokens
    ) {}
}
