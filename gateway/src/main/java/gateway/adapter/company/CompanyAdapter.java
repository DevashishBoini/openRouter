package gateway.adapter.company;

import gateway.dto.ChatCompletionRequest;
import gateway.dto.ChatCompletionResponse;

/**
 * Company-level adapter interface for LLM providers.
 *
 * <p>Company adapters handle company-specific API formats and transformations.
 * Each company adapter internally delegates to provider adapters for actual API calls.</p>
 *
 * <p>Architecture:</p>
 * <pre>
 * Request → CompanyAdapter (OpenAI/Anthropic/Google)
 *              ↓
 *           ProviderAdapter (OpenAI/Azure/Bedrock/etc.)
 *              ↓
 *           API Call
 * </pre>
 *
 * <p>Examples:</p>
 * <ul>
 *   <li><b>OpenAI Company:</b> Uses OpenAI provider (hardcoded for now, future: Azure, custom proxy)</li>
 *   <li><b>Anthropic Company:</b> Uses Anthropic provider (future: AWS Bedrock)</li>
 *   <li><b>Google Company:</b> Uses Google provider (future: Vertex AI)</li>
 * </ul>
 */
public interface CompanyAdapter {

    /**
     * Process a chat completion request using this company's API format.
     *
     * @param request the standardized chat completion request
     * @return the chat completion response
     */
    ChatCompletionResponse createChatCompletion(ChatCompletionRequest request);

    /**
     * Get the company name this adapter handles.
     *
     * @return lowercase company name (e.g., "openai", "anthropic", "google")
     */
    String getCompanyName();
}
