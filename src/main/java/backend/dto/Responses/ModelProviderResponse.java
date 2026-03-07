package backend.dto.Responses;

import java.util.UUID;

public record ModelProviderResponse(
        UUID providerId,
        String providerName,
        String providerWebsite,
        double inputTokenCost,
        double outputTokenCost
) {
}
