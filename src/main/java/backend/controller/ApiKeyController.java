package backend.controller;

import backend.annotation.CurrentUser;
import backend.annotation.SuccessMessage;
import backend.dto.Requests.ApiKeyCreateRequest;
import backend.dto.Requests.ApiKeyDisabledStatusUpdateRequest;
import backend.dto.Requests.LoginRequest;
import backend.dto.Responses.ApiKeyCreateResponse;
import backend.dto.Responses.ApiKeyGetResponse;
import backend.dto.Responses.LoginResponse;
import backend.exception.InvalidCredentialsException;
import backend.mapper.DtoMapper;
import backend.security.UserPrincipal;
import backend.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/api-keys")
@RequiredArgsConstructor
@Slf4j
public class ApiKeyController {

   private final ApiKeyService apiKeyService;
   private final DtoMapper dtoMapper;

    /**
     * Create a new API key for the authenticated user.
     *
     * @param user authenticated user extracted from the security context in {@link UserPrincipal} format
     * @param apiKeyCreateRequest request body containing API key details in {@link ApiKeyCreateRequest} format
     * @return created {@link ApiKeyCreateResponse} containing the generated API key value with HTTP status {@code 201 CREATED}
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


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("API Keys returned of the user")
    public List<ApiKeyGetResponse> getApiKeys(@CurrentUser UserPrincipal user) {

        log.info("getApiKeys - Request received: userId={}", user.userId());

        List<ApiKeyGetResponse> apiKeyGetResponseList = apiKeyService.getApiKeys(user.userId());

        log.info("getApiKeys - Response sent: userId={}", user.userId());

        return apiKeyGetResponseList;
    }

    @PatchMapping("/disable/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SuccessMessage("API key disabled status updated successfully")
    public void updateApiKeyDisabledStatus(
            @CurrentUser UserPrincipal user,
            @PathVariable UUID id,
            @RequestBody ApiKeyDisabledStatusUpdateRequest request
    ) {

        apiKeyService.updateApiKeyDisabledStatus(
                user.userId(),
                id,
                request.disabled()
        );
    }

    @PatchMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SuccessMessage("API key deleted successfully")
    public void deleteApiKey(
            @CurrentUser UserPrincipal user,
            @PathVariable UUID id
    ) {

        apiKeyService.deleteApiKey(
                user.userId(),
                id
        );
    }

}
