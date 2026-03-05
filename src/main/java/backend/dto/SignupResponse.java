package backend.dto;

import java.util.UUID;

public record SignupResponse(
        UUID id,
        String name,
        String email
) {}