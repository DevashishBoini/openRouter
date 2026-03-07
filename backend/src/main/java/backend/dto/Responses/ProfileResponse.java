package backend.dto.Responses;

import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String email,
        double credits
) {}
