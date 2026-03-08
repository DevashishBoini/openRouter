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
 * Company adapter for Anthropic models.
 *
 * <p>Handles all models from Anthropic company (Claude variants)
 * and delegates to the appropriate provider adapter.</p>
 *
 * <p><b>Current Implementation:</b> Hardcoded to use "anthropic" provider (not yet implemented).
 * <b>Future Enhancement:</b> Dynamically select provider based on routing logic
 * (e.g., Anthropic direct, AWS Bedrock, custom proxy)</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AnthropicCompanyAdapter implements CompanyAdapter {

    private final ProviderAdapterFactory providerAdapterFactory;

    @Override
    public ChatCompletionResponse createChatCompletion(ChatCompletionRequest request) {

        // TODO: Hardcoded to use "anthropic" provider for now
        // Future: Add dynamic provider selection logic (Anthropic direct vs AWS Bedrock vs proxy)
        String providerToUse = "anthropic";

        log.debug("Anthropic company adapter using provider: {}", providerToUse);

        ProviderAdapter providerAdapter = providerAdapterFactory.getAdapter(providerToUse);

        if (providerAdapter == null) {
            throw new ProviderException("Anthropic provider not yet implemented. Coming soon!");
        }

        return providerAdapter.createChatCompletion(request);
    }

    @Override
    public String getCompanyName() {
        return "anthropic";
    }
}
