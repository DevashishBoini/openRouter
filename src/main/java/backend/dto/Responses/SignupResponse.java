package backend.dto.Responses;

import java.util.UUID;

public record SignupResponse(
        UUID id,
        String email
) {}