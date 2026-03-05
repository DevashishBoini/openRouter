package backend.mapper;

import backend.dto.SignupRequest;
import backend.dto.SignupResponse;
import backend.dto.LoginRequest;
import backend.dto.LoginResponse;
import backend.dbModel.User;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(SignupRequest request);
    SignupResponse toUserResponse(User user);
    List<SignupResponse> toUserResponses(List<User> users);

    default LoginResponse toLoginResponse(String token) {
        return new LoginResponse(token);
    }

}