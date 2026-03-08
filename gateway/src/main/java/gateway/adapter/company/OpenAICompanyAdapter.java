package gateway.adapter.company;

import gateway.adapter.provider.ProviderAdapter;
import gateway.adapter.provider.ProviderAdapterFactory;
import gateway.dto.ChatCompletionRequest;
import gateway.dto.ChatCompletionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Company adapter for OpenAI models.
 *
 * <p>Handles all models from OpenAI company (gpt-4, gpt-3.5-turbo, etc.)
 * and delegates to the appropriate provider adapter.</p>
 *
 * <p><b>Current Implementation:</b> Hardcoded to use "openai" provider.
 * <b>Future Enhancement:</b> Dynamically select provider based on routing logic
 * (e.g., OpenAI direct, Azure OpenAI, custom proxy)</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAICompanyAdapter implements CompanyAdapter {

    private final ProviderAdapterFactory providerAdapterFactory;

    @Override
    public ChatCompletionResponse createChatCompletion(ChatCompletionRequest request) {

        // TODO: Hardcoded to use "openai" provider for now
        // Future: Add dynamic provider selection logic (OpenAI direct vs Azure vs proxy)
        String providerToUse = "openai";

        log.debug("OpenAI company adapter using provider: {}", providerToUse);

        ProviderAdapter providerAdapter = providerAdapterFactory.getAdapter(providerToUse);
        return providerAdapter.createChatCompletion(request);
    }

    @Override
    public String getCompanyName() {
        return "openai";
    }
}
