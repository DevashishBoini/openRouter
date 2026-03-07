package backend.dto.Responses;

import java.util.UUID;

public record ModelResponse(
        UUID id,
        String name,
        String slug,
        UUID companyId,
        String companyName
) {
}
