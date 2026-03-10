package backend.dto.Responses;

import java.math.BigDecimal;

public record UsageSummaryResponse(
        Long totalRequests,
        Long totalTokens,
        BigDecimal totalCost,
        Double avgLatency,
        Double successRate
) {}
