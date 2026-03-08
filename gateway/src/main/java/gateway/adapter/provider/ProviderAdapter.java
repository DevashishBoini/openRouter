package gateway.adapter.provider;

import gateway.dto.ChatCompletionRequest;
import gateway.dto.ChatCompletionResponse;

/**
 * Strategy interface for LLM provider adapters.
 *
 * <p>All provider implementations (OpenAI, Anthropic, Gemini, etc.) must
 * implement this interface. The adapter is responsible for transforming
 * requests, calling the provider API, and normalizing responses.</p>
 *
 * <p>Provider adapters are the low-level implementation that actually makes API calls.
 * They are used by Company adapters to execute requests.</p>
 */
public interface ProviderAdapter {

    /**
     * Calls the provider's chat completion API.
     *
     * @param request the chat completion request (OpenAI format)
     * @return the chat completion response (normalized to OpenAI format)
     */
    ChatCompletionResponse createChatCompletion(ChatCompletionRequest request);

    /**
     * Returns the name of the provider this adapter handles.
     *
     * @return provider name (e.g., "openai", "anthropic", "google")
     */
    String getProviderName();
}
