package backend.controller;

import backend.dbModel.User;
import backend.service.UserService;

import backend.exception.EmailAlreadyExistsException;

import backend.dto.CreateUserRequest;
import backend.dto.UserResponse;
import backend.mapper.UserMapper;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * REST controller responsible for handling HTTP requests related to User resources.
 *
 * <p>This controller exposes endpoints for retrieving users and creating new users.</p>
 *
 * <p>Request DTOs are converted into domain entities using {@link UserMapper},
 * and entities returned from the service layer are mapped into response DTOs
 * before being returned to the client.</p>
 *
 * <p>All exceptions thrown by the service layer are handled centrally by the
 * {@code GlobalExceptionHandler}.</p>
 *
 * <p>Base URL: <code>/api/users</code></p>
 */


@RestController
@RequestMapping("/api/v1/users")
public class AuthController {


    private final UserService userService;
    private final UserMapper userMapper;


    /**
     * Constructs a {@code AuthController} with the required dependencies.
     *
     * @param userService service layer responsible for user-related business logic
     * @param userMapper mapper used to convert between entities and DTOs
     */
    public AuthController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }



    /**
     * Create a new user.
     *
     *
     * @param request request body containing user creation details in {@link CreateUserRequest} format
     * @return ResponseEntity containing the created {@link UserResponse} with HTTP status {@code 201 CREATED}
     * @throws EmailAlreadyExistsException if a user with the given email already exists
     */
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {

//        User user = userMapper.toUser(request);
        User createdUser = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserResponse(createdUser));
    }

}
