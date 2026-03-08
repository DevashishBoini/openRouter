package gateway.dto;

public record ErrorResponse(
        boolean success,
        String message,
        Object data
) {}
