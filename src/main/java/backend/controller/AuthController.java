package backend.controller;

import backend.annotation.SuccessMessage;
import backend.dbModel.User;
import backend.mapper.DtoMapper;
import backend.service.UserService;

import backend.exception.EmailAlreadyExistsException;
import backend.exception.InvalidCredentialsException;

import backend.dto.Requests.SignupRequest;
import backend.dto.Responses.SignupResponse;
import backend.dto.Requests.LoginRequest;
import backend.dto.Responses.LoginResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


/**
 * REST controller responsible for handling HTTP requests related to User resources.
 *
 * <p>This controller exposes endpoints for retrieving users and creating new users.</p>
 *
 * <p>Request DTOs are converted into domain entities using {@link DtoMapper},
 * and entities returned from the service layer are mapped into response DTOs
 * before being returned to the client.</p>
 *
 * <p>All exceptions thrown by the service layer are handled centrally by the
 * {@code GlobalExceptionHandler}.</p>
 *
 * <p>Base URL: <code>/api/users</code></p>
 */


@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final DtoMapper dtoMapper;



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

        log.info("createUser - Request received: email={}", request.email());

        User createdUser = userService.createUser(request.email(), request.password());

        log.info("createUser - Response sent: userId={}", createdUser.getId());

        return dtoMapper.toUserResponse(createdUser);
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

        log.info("login- Request received: email={}", request.email());

        String token = userService.userLogin(request.email(), request.password());

        log.info("login - Response sent: email={}", request.email());

        return dtoMapper.toLoginResponse(token);
    }
}
