package backend.mapper;

import backend.dto.Requests.SignupRequest;
import backend.dto.Responses.LoginResponse;
import backend.dto.Responses.ProfileResponse;
import backend.dto.Responses.SignupResponse;
import backend.dbModel.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public User toUser(SignupRequest request) {
        return User.builder()
                .email(request.email())
                .passwordHash(request.password())
                .build();
    }

    public SignupResponse toUserResponse(User user) {
        return new SignupResponse(
                user.getId(),
                user.getEmail()
        );
    }

    public List<SignupResponse> toUserResponses(List<User> users) {
        return users.stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    public LoginResponse toLoginResponse(String token) {
        return new LoginResponse(token);
    }

    public ProfileResponse toProfileResponse(User user) {
        return new ProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getCredits()
        );
    }
}