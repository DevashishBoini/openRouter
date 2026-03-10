package backend.dto.Responses;

import java.math.BigDecimal;
import java.util.List;

public record CostBreakdownResponse(
        List<BreakdownItem> byProvider,
        List<BreakdownItem> byModel
) {
    public record BreakdownItem(
            String name,
            Long requestCount,
            BigDecimal totalCost,
            Double percentage
    ) {}
}
