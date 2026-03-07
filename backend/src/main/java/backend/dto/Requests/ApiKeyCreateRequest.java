package backend.dto.Requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ApiKeyCreateRequest(
        @NotBlank(message = "Api Key Name cannot be empty")
        String apiKeyName
)
{}
