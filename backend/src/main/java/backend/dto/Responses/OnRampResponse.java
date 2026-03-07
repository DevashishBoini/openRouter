package backend.dto.Responses;

import java.util.UUID;

public record OnRampResponse(
        UUID transactionId,
        double amount,
        double newBalance
) {
}
