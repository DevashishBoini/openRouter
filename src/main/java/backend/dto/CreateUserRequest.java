package backend.dto;

public record CreateUserRequest(
        String name,
        String email,
        String password
) {}