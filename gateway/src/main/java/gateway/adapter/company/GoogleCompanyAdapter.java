package gateway.adapter.company;

import gateway.adapter.provider.ProviderAdapter;
import gateway.adapter.provider.ProviderAdapterFactory;
import gateway.dto.ChatCompletionRequest;
import gateway.dto.ChatCompletionResponse;
import gateway.exception.ProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Company adapter for Google models.
 *
 * <p>Handles all models from Google company (Gemini variants)
 * and delegates to the appropriate provider adapter.</p>
 *
 * <p><b>Current Implementation:</b> Hardcoded to use "google" provider (not yet implemented).
 * <b>Future Enhancement:</b> Dynamically select provider based on routing logic
 * (e.g., Google direct, Vertex AI, custom proxy)</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleCompanyAdapter implements CompanyAdapter {

    private final ProviderAdapterFactory providerAdapterFactory;

    @Override
    public ChatCompletionResponse createChatCompletion(ChatCompletionRequest request) {

        // TODO: Hardcoded to use "google" provider for now
        // Future: Add dynamic provider selection logic (Google direct vs Vertex AI vs proxy)
        String providerToUse = "google";

        log.debug("Google company adapter using provider: {}", providerToUse);

        ProviderAdapter providerAdapter = providerAdapterFactory.getAdapter(providerToUse);

        if (providerAdapter == null) {
            throw new ProviderException("Google provider not yet implemented. Coming soon!");
        }

        return providerAdapter.createChatCompletion(request);
    }

    @Override
    public String getCompanyName() {
        return "google";
    }
}
