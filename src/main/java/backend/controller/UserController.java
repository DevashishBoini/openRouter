package backend.controller;

import backend.model.User;
import backend.service.UserService;

import backend.exception.ResourceNotFoundException;
import backend.exception.EmailAlreadyExistsException;

import backend.dto.CreateUserRequest;
import backend.dto.UserResponse;
import backend.mapper.UserMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;





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
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;
    private final UserMapper userMapper;


    /**
     * Constructs a {@code UserController} with the required dependencies.
     *
     * @param userService service layer responsible for user-related business logic
     * @param userMapper mapper used to convert between entities and DTOs
     */
    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }



    /**
     * Retrieve all users in the system.
     *
     * <p>The service layer returns a list of {@link User} entities which are mapped
     * to {@link UserResponse} DTOs before being returned in the HTTP response.</p>
     *
     * @return ResponseEntity containing a list of {@link UserResponse} and HTTP status 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(userMapper.toUserResponses(users));
    }



    /**
     * Retrieve a user by their unique identifier.
     *
     * <p>The service layer retrieves the corresponding {@link User} entity,
     * which is then mapped to a {@link UserResponse} DTO.</p>
     *
     * @param id UUID of the user to retrieve
     * @return ResponseEntity containing the {@link UserResponse} and HTTP status 200 (OK)
     * @throws ResourceNotFoundException if no user exists with the given id
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {

        User user = userService.getUserById(id);

        return ResponseEntity.ok(userMapper.toUserResponse(user));
    }



    /**
     * Retrieve a user by their email address.
     *
     * <p>The service layer retrieves the corresponding {@link User} entity
     * using the provided email address. The entity is then mapped to a
     * {@link UserResponse} DTO before being returned to the client.</p>
     *
     * @param email email address used to identify the user
     * @return ResponseEntity containing the {@link UserResponse} and HTTP status 200 (OK)
     * @throws ResourceNotFoundException if no user exists with the given email
     */
    @GetMapping("/by-email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {

        User user = userService.getUserByEmail(email);

        return ResponseEntity.ok(userMapper.toUserResponse(user));
    }



    /**
     * Create a new user.
     *
     * <p>The incoming {@link CreateUserRequest} DTO is mapped to a {@link User}
     * entity using {@link UserMapper}. The entity is then persisted by the
     * service layer and mapped back to a {@link UserResponse} DTO.</p>
     *
     * @param request request body containing user creation details
     * @return ResponseEntity containing the created {@link UserResponse} and HTTP status 201 (CREATED)
     * @throws EmailAlreadyExistsException if a user with the given email already exists
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {

        User user = userMapper.toUser(request);
        User createdUser = userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toUserResponse(createdUser));
    }
}