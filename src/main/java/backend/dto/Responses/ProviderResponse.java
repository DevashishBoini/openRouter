package backend.dto.Responses;

import java.util.UUID;

public record ProviderResponse(
        UUID id,
        String name,
        String website
) {
}
