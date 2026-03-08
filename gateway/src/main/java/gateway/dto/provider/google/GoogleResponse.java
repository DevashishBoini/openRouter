package gateway.dto.provider.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Google Gemini-specific response DTO.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleResponse(
        List<Candidate> candidates,
        @JsonProperty("usageMetadata") UsageMetadata usageMetadata,
        @JsonProperty("modelVersion") String modelVersion
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Candidate(
            Content content,
            @JsonProperty("finishReason") String finishReason,
            Integer index,
            @JsonProperty("safetyRatings") List<Object> safetyRatings
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Content(List<Part> parts, String role) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Part(String text) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record UsageMetadata(
            @JsonProperty("promptTokenCount") Integer promptTokenCount,
            @JsonProperty("candidatesTokenCount") Integer candidatesTokenCount,
            @JsonProperty("totalTokenCount") Integer totalTokenCount
    ) {}
}
