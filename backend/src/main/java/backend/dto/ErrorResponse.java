package backend.dto;

public record ErrorResponse(
        boolean success,
        String message,
        Object details
) implements BaseApiResponse {}