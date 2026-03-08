package gateway.service;

import gateway.dbModel.Model;
import gateway.dbModel.ModelProviderMapping;
import gateway.dto.ModelSlug;
import gateway.exception.ModelNotFoundException;
import gateway.repository.ModelProviderMappingRepository;
import gateway.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for routing LLM requests to find the appropriate model-provider mapping.
 *
 * <p>Parses model slugs and queries the database for model information and pricing.
 * The slug format is validated, and the model name is extracted for database lookup.</p>
 *
 * <p>Supported slug formats:</p>
 * <ul>
 *   <li>openai/gpt-4</li>
 *   <li>anthropic/claude-3-haiku</li>
 *   <li>gpt-4 (no prefix, defaults to openai)</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouterService {

    private final ModelRepository modelRepository;
    private final ModelProviderMappingRepository modelProviderMappingRepository;

    /**
     * Finds the model-provider mapping for the given model slug.
     *
     * <p>Parses the slug, extracts the model name, queries the database for the model,
     * and returns the first available provider mapping with pricing information.</p>
     *
     * <p>Errors:</p>
     * <ul>
     *   <li>Unrecognized slug format → ModelNotFoundException</li>
     *   <li>Model not in database → ModelNotFoundException</li>
     *   <li>No provider mappings → ModelNotFoundException</li>
     * </ul>
     *
     * @param slugString the model slug from the request (e.g., "openai/gpt-4", "gpt-4")
     * @return the ModelProviderMapping containing model, provider, and pricing info
     * @throws ModelNotFoundException if validation fails or model is not found
     */
    public ModelProviderMapping findProviderMapping(String slugString) {

        log.info("Finding provider mapping for slug: {}", slugString);

        // Parse and validate slug format
        // Throws ModelNotFoundException if format is invalid or provider is unrecognized
        ModelSlug slug = new ModelSlug(slugString);
        String modelName = slug.getModelName();

        log.debug("Parsed slug: providerHint={}, model={}", slug.getProviderHint(), modelName);

        // Find model by full slug in database (includes provider prefix)
        Model model = modelRepository.findBySlug(slugString)
                .orElseThrow(() -> {
                    log.warn("Model not found in database: {}", slugString);
                    return new ModelNotFoundException("Model not found: " + slugString);
                });

        // Find all provider mappings for this model
        List<ModelProviderMapping> mappings = modelProviderMappingRepository
                .findByModelId(model.getId());

        if (mappings.isEmpty()) {
            log.warn("No provider mappings found for model: {}", slugString);
            throw new ModelNotFoundException("No providers available for model: " + slugString);
        }

        // TODO: Currently returns first available mapping
        // Future: Add routing logic (cost-based, load balancing, failover)
        ModelProviderMapping selectedMapping = mappings.get(0);

        log.info("Provider mapping found: model={}, provider={}, inputCost={}, outputCost={}",
                slugString,
                selectedMapping.getProvider().getName(),
                selectedMapping.getInputTokenCost(),
                selectedMapping.getOutputTokenCost());

        return selectedMapping;
    }
}
