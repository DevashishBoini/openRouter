package gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Standard chat completion request DTO.
 *
 * <p>This is the normalized request format accepted from clients.
 * It gets transformed into provider-specific formats by adapters.
 * Unknown fields are ignored for forward compatibility.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatCompletionRequest(
        @NotBlank
        String model,

        @NotNull
        List<ChatMessage> messages,

        @JsonProperty("temperature")
        Double temperature,

        @JsonProperty("max_tokens")
        Integer maxTokens,

        @JsonProperty("top_p")
        Double topP,

        @JsonProperty("frequency_penalty")
        Double frequencyPenalty,

        @JsonProperty("presence_penalty")
        Double presencePenalty,

        @JsonProperty("stop")
        List<String> stop,

        @JsonProperty("stream")
        Boolean stream,

        @JsonProperty("user")
        String user
) {}
