package gateway.dto.provider.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * OpenAI-specific request DTO for chat completions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenAIRequest(
        String model,
        List<Message> messages,
        Double temperature,
        @JsonProperty("max_completion_tokens") Integer maxCompletionTokens,
        @JsonProperty("top_p") Double topP,
        @JsonProperty("frequency_penalty") Double frequencyPenalty,
        @JsonProperty("presence_penalty") Double presencePenalty,
        List<String> stop,
        Boolean stream,
        String user
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(String role, String content, String name) {}
}
