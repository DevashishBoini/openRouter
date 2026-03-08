package gateway.dto.provider.anthropic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Anthropic-specific response DTO for messages API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AnthropicResponse(
        String id,
        String type,
        String role,
        List<Content> content,
        String model,
        @JsonProperty("stop_reason") String stopReason,
        @JsonProperty("stop_sequence") String stopSequence,
        Usage usage
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Content(String type, String text) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(
            @JsonProperty("input_tokens") Integer inputTokens,
            @JsonProperty("output_tokens") Integer outputTokens
    ) {}
}
