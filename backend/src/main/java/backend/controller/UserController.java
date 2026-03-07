package backend.controller;

import backend.annotation.CurrentUser;
import backend.annotation.SuccessMessage;
import backend.dto.Responses.ProfileResponse;
import backend.mapper.DtoMapper;
import backend.security.UserPrincipal;
import backend.service.UserService;
import backend.dbModel.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller responsible for handling HTTP requests related to User profile operations.
 *
 * <p>This controller exposes endpoints for retrieving and managing user profile information.</p>
 *
 * <p>All endpoints require authentication via JWT token.</p>
 *
 * <p>All exceptions thrown by the service layer are handled centrally by the
 * {@code GlobalExceptionHandler}.</p>
 *
 * <p>Base URL: <code>/api/v1/user</code></p>
 */
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final DtoMapper dtoMapper;

    /**
     * Retrieve the authenticated user's profile.
     *
     * <p>Returns the user's profile information including their unique ID, email address,
     * and current credit balance.</p>
     *
     * @param user authenticated user extracted from the security context
     * @return {@link ProfileResponse} containing user profile details with HTTP status {@code 200 OK}
     */
    @GetMapping("/profile")
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("User profile retrieved successfully")
    public ProfileResponse getUserProfile(@CurrentUser UserPrincipal user) {

        log.info("getUserProfile - Request received: userId={}", user.userId());

        User userEntity = userService.getUserById(user.userId());

        log.info("getUserProfile - Response sent: userId={}", user.userId());

        return dtoMapper.toProfileResponse(userEntity);
    }
}
