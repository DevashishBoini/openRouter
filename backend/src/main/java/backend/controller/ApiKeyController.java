package backend.controller;

import backend.annotation.CurrentUser;
import backend.annotation.SuccessMessage;
import backend.dto.Requests.ApiKeyCreateRequest;
import backend.dto.Requests.ApiKeyDisabledStatusUpdateRequest;
import backend.dto.Responses.ApiKeyCreateResponse;
import backend.dto.Responses.ApiKeyGetResponse;
import backend.security.UserPrincipal;
import backend.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller responsible for handling HTTP requests related to API Key management.
 *
 * <p>This controller exposes endpoints for creating, retrieving, disabling, and deleting
 * API keys for authenticated users. API keys are used to authenticate requests to the
 * application's API endpoints.</p>
 *
 * <p>All endpoints require authentication via JWT token. API keys returned do not include
 * the actual key value (only shown once during creation) for security purposes.</p>
 *
 * <p>All exceptions thrown by the service layer are handled centrally by the
 * {@code GlobalExceptionHandler}.</p>
 *
 * <p>Base URL: <code>/api/v1/api-keys</code></p>
 */
@RestController
@RequestMapping("/api/v1/api-keys")
@RequiredArgsConstructor
@Slf4j
public class ApiKeyController {

   private final ApiKeyService apiKeyService;

    /**
     * Create a new API key for the authenticated user.
     *
     * <p>Generates a new API key with a unique identifier and securely stores a hashed
     * version in the database. The raw API key value is returned only once in the response
     * and cannot be retrieved again for security reasons.</p>
     *
     * <p><b>IMPORTANT:</b> Save the returned API key value securely as it will not be shown again.</p>
     *
     * @param user authenticated user extracted from the security context
     * @param apiKeyCreateRequest request body containing the API key name for identification
     * @return {@link ApiKeyCreateResponse} containing the generated API key value with HTTP status {@code 201 CREATED}
     */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @SuccessMessage("API key created successfully. Save it securely — it will not be shown again.")
    public ApiKeyCreateResponse createApiKey(
            @CurrentUser UserPrincipal user,
            @Valid @RequestBody ApiKeyCreateRequest apiKeyCreateRequest) {

        log.info("createApiKey - Request received: userId={}, apiKeyName={}",
                user.userId(), apiKeyCreateRequest.apiKeyName());

        String rawApiKey = apiKeyService.createApiKey(
                user.userId(),
                apiKeyCreateRequest.apiKeyName()
        );

        log.info("createApiKey - Response sent: userId={}", user.userId());

        return new ApiKeyCreateResponse(rawApiKey);
    }


    /**
     * Retrieve all API keys for the authenticated user.
     *
     * <p>Returns a list of all non-deleted API keys belonging to the authenticated user.
     * The response includes metadata such as the key name, disabled status, credits consumed,
     * last used timestamp, and creation timestamp.</p>
     *
     * <p><b>Note:</b> The actual API key values are not included in the response for security reasons.
     * Only the key metadata is returned.</p>
     *
     * @param user authenticated user extracted from the security context
     * @return list of {@link ApiKeyGetResponse} containing API key metadata with HTTP status {@code 200 OK}
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("API keys retrieved successfully")
    public List<ApiKeyGetResponse> getApiKeys(@CurrentUser UserPrincipal user) {

        log.info("getApiKeys - Request received: userId={}", user.userId());

        List<ApiKeyGetResponse> apiKeyGetResponseList = apiKeyService.getApiKeys(user.userId());

        log.info("getApiKeys - Response sent: userId={}", user.userId());

        return apiKeyGetResponseList;
    }

    /**
     * Update the disabled status of an API key.
     *
     * <p>Allows the authenticated user to enable or disable one of their API keys.
     * Disabled API keys cannot be used to authenticate API requests but can be re-enabled later.
     * This provides a temporary way to revoke access without permanently deleting the key.</p>
     *
     * @param user authenticated user extracted from the security context
     * @param id the UUID of the API key to update
     * @param request request body containing the new disabled status (true to disable, false to enable)
     * @throws backend.exception.ResourceNotFoundException if the API key is not found or doesn't belong to the user
     */
    @PatchMapping("/disable/{id}")
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("API key disabled status updated successfully")
    public Object updateApiKeyDisabledStatus(
            @CurrentUser UserPrincipal user,
            @PathVariable UUID id,
            @RequestBody ApiKeyDisabledStatusUpdateRequest request
    ) {

        apiKeyService.updateApiKeyDisabledStatus(
                user.userId(),
                id,
                request.disabled()
        );

        return Map.of();
    }

    /**
     * Soft delete an API key.
     *
     * <p>Marks the specified API key as deleted in the database. This is a soft delete operation,
     * meaning the key is not physically removed from the database but is marked as deleted and
     * will no longer appear in API key listings or be usable for authentication.</p>
     *
     * <p><b>Note:</b> This operation is permanent and cannot be undone. The deleted API key
     * cannot be recovered or re-enabled.</p>
     *
     * @param user authenticated user extracted from the security context
     * @param id the UUID of the API key to delete
     * @throws backend.exception.ResourceNotFoundException if the API key is not found or doesn't belong to the user
     */
    @PatchMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("API key deleted successfully")
    public Object deleteApiKey(
            @CurrentUser UserPrincipal user,
            @PathVariable UUID id
    ) {

        apiKeyService.deleteApiKey(
                user.userId(),
                id
        );

        return Map.of();
    }

}
