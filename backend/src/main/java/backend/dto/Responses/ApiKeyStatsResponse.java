package backend.dto.Responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ApiKeyStatsResponse(
        UUID apiKeyId,
        String apiKeyName,
        Long requestCount,
        BigDecimal totalCost,
        LocalDateTime lastUsed
) {}
