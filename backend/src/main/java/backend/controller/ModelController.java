package backend.controller;

import backend.annotation.SuccessMessage;
import backend.dto.Responses.ModelProviderResponse;
import backend.dto.Responses.ModelResponse;
import backend.dto.Responses.ProviderResponse;
import backend.service.ModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller responsible for handling HTTP requests related to Model, Provider,
 * and ModelProviderMapping resources.
 *
 * <p>This controller exposes endpoints for retrieving models, providers, and their mappings.
 * No POST endpoints are exposed as these resources are managed directly by admins in the database.</p>
 *
 * <p>Base URL: <code>/api/v1/models</code></p>
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ModelController {

    private final ModelService modelService;

    /**
     * Get all models.
     *
     * @return list of {@link ModelResponse} with HTTP status {@code 200 OK}
     */
    @GetMapping("/models")
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("Models retrieved successfully")
    public List<ModelResponse> getAllModels() {
        log.info("getAllModels - Request received");
        List<ModelResponse> models = modelService.getAllModels();
        log.info("getAllModels - Response sent: {} models", models.size());
        return models;
    }

    /**
     * Get all providers.
     *
     * @return list of {@link ProviderResponse} with HTTP status {@code 200 OK}
     */
    @GetMapping("/providers")
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("Providers retrieved successfully")
    public List<ProviderResponse> getAllProviders() {
        log.info("getAllProviders - Request received");
        List<ProviderResponse> providers = modelService.getAllProviders();
        log.info("getAllProviders - Response sent: {} providers", providers.size());
        return providers;
    }

    /**
     * Get all providers for a specific model.
     *
     * @param modelId the ID of the model
     * @return list of {@link ModelProviderResponse} with HTTP status {@code 200 OK}
     */
    @GetMapping("/models/{modelId}/providers")
    @ResponseStatus(HttpStatus.OK)
    @SuccessMessage("Providers for model retrieved successfully")
    public List<ModelProviderResponse> getProvidersByModelId(@PathVariable UUID modelId) {
        log.info("getProvidersByModelId - Request received: modelId={}", modelId);
        List<ModelProviderResponse> providers = modelService.getProvidersByModelId(modelId);
        log.info("getProvidersByModelId - Response sent: {} providers", providers.size());
        return providers;
    }
}
