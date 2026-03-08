package gateway.dto.provider.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Google Gemini-specific request DTO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoogleRequest(
        List<Content> contents,
        @JsonProperty("systemInstruction") SystemInstruction systemInstruction,
        @JsonProperty("generationConfig") GenerationConfig generationConfig
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Content(String role, List<Part> parts) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Part(String text) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SystemInstruction(List<Part> parts) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record GenerationConfig(
            Double temperature,
            @JsonProperty("topP") Double topP,
            @JsonProperty("topK") Integer topK,
            @JsonProperty("maxOutputTokens") Integer maxOutputTokens,
            @JsonProperty("stopSequences") List<String> stopSequences
    ) {}
}
