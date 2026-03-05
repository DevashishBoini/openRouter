package backend.controller;

import backend.annotation.SuccessMessage;
import backend.dbModel.User;
import backend.service.UserService;

import backend.exception.EmailAlreadyExistsException;
import backend.exception.InvalidCredentialsException;

import backend.dto.Requests.SignupRequest;
import backend.dto.Responses.SignupResponse;
import backend.dto.Requests.LoginRequest;
import backend.dto.Responses.LoginResponse;
import backend.mapper.UserMapper;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/auth/v1")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

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
     * @param request request body containing user creation details in {@link SignupRequest} format
     * @return created {@link SignupResponse} with HTTP status {@code 201 CREATED}
     * @throws EmailAlreadyExistsException if a user with the given email already exists
     */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    @SuccessMessage("User created successfully")
    public SignupResponse createUser(@Valid @RequestBody SignupRequest request) {

        logger.info("POST /api/v1/users/signup - Request received: email={}", request.email());

        User createdUser = userService.createUser(request.email(), request.password());

        logger.info("POST /api/v1/users/signup - Response sent: userId={}, status=201", createdUser.getId());

        return userMapper.toUserResponse(createdUser);
    }


    /**
     * Authenticate a user and generate a JWT token.
     *
     * @param request request body containing user login credentials in {@link LoginRequest} format
     * @return generated {@link LoginResponse} with HTTP status {@code 200 OK}
     * @throws InvalidCredentialsException if the provided email or password is incorrect
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("Login successful")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {

        logger.info("POST /api/v1/users/login - Request received: email={}", request.email());

        String token = userService.userLogin(request.email(), request.password());

        logger.info("POST /api/v1/users/login - Response sent: email={}, status=200", request.email());

        return userMapper.toLoginResponse(token);
    }
}
