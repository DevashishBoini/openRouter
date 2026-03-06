package backend.dto.Responses;

import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyGetResponse(
        UUID id,
        String name,
        boolean disabled,
        double creditsConsumed,
        LocalDateTime lastUsed,
        LocalDateTime createdAt
){}
