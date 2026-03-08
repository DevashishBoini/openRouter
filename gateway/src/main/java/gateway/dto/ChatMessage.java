package gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

/**
 * Standard chat message DTO used across all providers.
 *
 * <p>This is the normalized format used for both request and response messages.
 * Unknown fields from provider APIs are ignored to handle API evolution.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChatMessage(
        @NotBlank
        String role,

        @NotBlank
        String content
) {}
