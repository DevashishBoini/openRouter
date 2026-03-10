package backend.dto.Responses;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TimelineDataPoint(
        LocalDate date,
        Long requestCount,
        BigDecimal totalCost,
        Long totalTokens
) {}
