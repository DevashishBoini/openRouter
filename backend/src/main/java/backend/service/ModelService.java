package backend.service;

import backend.dbModel.Model;
import backend.dbModel.ModelProviderMapping;
import backend.dbModel.Provider;
import backend.dto.Responses.ModelProviderResponse;
import backend.dto.Responses.ModelResponse;
import backend.dto.Responses.ProviderResponse;
import backend.exception.ResourceNotFoundException;
import backend.repository.ModelProviderMappingRepository;
import backend.repository.ModelRepository;
import backend.repository.ProviderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModelService {

    private final ModelRepository modelRepository;
    private final ProviderRepository providerRepository;
    private final ModelProviderMappingRepository modelProviderMappingRepository;

    public List<ModelResponse> getAllModels() {
        List<Model> models = modelRepository.findAll();
        log.info("Fetched {} models", models.size());

        return models.stream()
                .map(model -> new ModelResponse(
                        model.getId(),
                        model.getName(),
                        model.getSlug(),
                        model.getCompany().getId(),
                        model.getCompany().getName()
                ))
                .toList();
    }

    public List<ProviderResponse> getAllProviders() {
        List<Provider> providers = providerRepository.findAll();
        log.info("Fetched {} providers", providers.size());

        return providers.stream()
                .map(provider -> new ProviderResponse(
                        provider.getId(),
                        provider.getName(),
                        provider.getWebsite()
                ))
                .toList();
    }

    public List<ModelProviderResponse> getProvidersByModelId(UUID modelId) {
        // Verify model exists
        modelRepository.findById(modelId)
                .orElseThrow(() -> new ResourceNotFoundException("Model not found"));

        List<ModelProviderMapping> mappings = modelProviderMappingRepository.findByModelId(modelId);
        log.info("Fetched {} providers for model {}", mappings.size(), modelId);

        return mappings.stream()
                .map(mapping -> new ModelProviderResponse(
                        mapping.getProvider().getId(),
                        mapping.getProvider().getName(),
                        mapping.getProvider().getWebsite(),
                        mapping.getInputTokenCost(),
                        mapping.getOutputTokenCost()
                ))
                .toList();
    }
}
