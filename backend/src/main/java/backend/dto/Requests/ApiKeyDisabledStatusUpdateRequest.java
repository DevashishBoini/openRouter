package backend.dto.Requests;

import jakarta.validation.constraints.NotBlank;

public record ApiKeyDisabledStatusUpdateRequest(

        @NotBlank(message = "disabled")
        boolean disabled
) {}
