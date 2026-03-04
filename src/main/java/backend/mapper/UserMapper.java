package backend.mapper;

import backend.dto.CreateUserRequest;
import backend.dto.UserResponse;
import backend.model.User;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(CreateUserRequest request);
    UserResponse toUserResponse(User user);
    List<UserResponse> toUserResponses(List<User> users);

}