package backend.dto;

public record SuccessResponse<T>(
        boolean success,
        String message,
        T data
) implements BaseApiResponse {}