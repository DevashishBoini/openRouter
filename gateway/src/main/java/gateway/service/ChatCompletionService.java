package gateway.service;

import gateway.adapter.company.CompanyAdapter;
import gateway.adapter.company.CompanyAdapterFactory;
import gateway.dbModel.ApiKey;
import gateway.dbModel.ModelProviderMapping;
import gateway.dbModel.User;
import gateway.dto.ChatCompletionRequest;
import gateway.dto.ChatCompletionResponse;
import gateway.dto.ModelSlug;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for processing chat completion requests.
 *
 * <p>This service orchestrates the entire flow of processing a chat completion request:
 * <ol>
 *   <li>Parse and validate the model slug</li>
 *   <li>Find the model and provider mapping from the database</li>
 *   <li>Estimate and check credits</li>
 *   <li>Route to the appropriate company/provider adapter</li>
 *   <li>Call the LLM provider API</li>
 *   <li>Calculate actual cost and deduct credits</li>
 *   <li>Save conversation history</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatCompletionService {

    private final RouterService routerService;
    private final CreditService creditService;
    private final CompanyAdapterFactory companyAdapterFactory;
    private final ConversationService conversationService;
    private final RateLimitService rateLimitService;

    /**
     * Process a chat completion request from start to finish.
     *
     * @param request the chat completion request
     * @param user the authenticated user
     * @param apiKey the API key used for authentication
     * @return the chat completion response from the LLM provider
     */
    @Transactional
    public ChatCompletionResponse processCompletion(
            ChatCompletionRequest request,
            User user,
            ApiKey apiKey) {

        long startTime = System.currentTimeMillis();

        log.info("Processing chat completion: userId={}, apiKeyId={}, model={}",
                user.getId(), apiKey.getId(), request.model());

        // Step 1: Check rate limit
        rateLimitService.checkRateLimit(apiKey.getId());

        // Step 2: Parse and validate model slug
        ModelSlug slug = parseModelSlug(request.model());

        // Step 3: Find model and provider mapping in database
        ModelProviderMapping mapping = findModelMapping(request.model());

        // Step 4: Get company name from model
        String companyName = extractCompanyName(mapping);

        // Step 5: Estimate cost and check if user has sufficient credits
        checkCreditsAvailability(request, user, mapping);

        // Step 6: Route to appropriate company adapter and call LLM provider
        ChatCompletionResponse response = callLLMProvider(request, companyName, slug);

        // Step 7: Calculate actual cost and deduct from user credits
        double actualCost = deductCreditsForCompletion(response, user, apiKey, mapping);

        // Step 8: Calculate response time
        int responseTimeMs = calculateResponseTime(startTime);

        // Step 9: Save conversation history
        saveConversationHistory(request, response, user, apiKey, mapping, actualCost, responseTimeMs);

        log.info("Chat completion successful: userId={}, model={}, company={}, tokens={}/{}, cost={}, time={}ms",
                user.getId(),
                slug.getModelName(),
                companyName,
                response.usage().promptTokens(),
                response.usage().completionTokens(),
                actualCost,
                responseTimeMs);

        return response;
    }

    /**
     * Parse and validate the model slug format.
     */
    private ModelSlug parseModelSlug(String modelSlug) {
        log.debug("Parsing model slug: {}", modelSlug);
        ModelSlug slug = new ModelSlug(modelSlug);
        log.debug("Parsed slug: providerHint={}, modelName={}", slug.getProviderHint(), slug.getModelName());
        return slug;
    }

    /**
     * Find the model and provider mapping from the database.
     */
    private ModelProviderMapping findModelMapping(String modelSlug) {
        log.debug("Finding provider mapping for model: {}", modelSlug);
        return routerService.findProviderMapping(modelSlug);
    }

    /**
     * Extract company name from the model mapping.
     */
    private String extractCompanyName(ModelProviderMapping mapping) {
        String companyName = mapping.getModel().getCompany().getName();
        log.debug("Model company: {}", companyName);
        return companyName;
    }

    /**
     * Estimate cost and check if user has sufficient credits.
     */
    private void checkCreditsAvailability(
            ChatCompletionRequest request,
            User user,
            ModelProviderMapping mapping) {

        int estimatedTokens = request.maxTokens() != null ? request.maxTokens() : 1000;

        double estimatedCost = creditService.calculateCost(
                estimatedTokens / 2,  // Rough estimate for input
                estimatedTokens / 2,  // Rough estimate for output
                mapping
        );

        log.debug("Estimated cost: {} credits for ~{} tokens", estimatedCost, estimatedTokens);

        creditService.checkSufficientCredits(user, estimatedCost);
    }

    /**
     * Route to the appropriate company adapter and call the LLM provider.
     */
    private ChatCompletionResponse callLLMProvider(
            ChatCompletionRequest request,
            String companyName,
            ModelSlug slug) {

        CompanyAdapter companyAdapter = companyAdapterFactory.getAdapter(companyName);

        log.debug("Using company adapter: {} for model: {}",
                companyAdapter.getClass().getSimpleName(), slug.getModelName());

        return companyAdapter.createChatCompletion(request);
    }

    /**
     * Calculate actual cost based on token usage and deduct from user credits.
     */
    private double deductCreditsForCompletion(
            ChatCompletionResponse response,
            User user,
            ApiKey apiKey,
            ModelProviderMapping mapping) {

        double actualCost = creditService.calculateCost(
                response.usage().promptTokens(),
                response.usage().completionTokens(),
                mapping
        );

        log.debug("Actual cost: {} credits for {}/{} tokens",
                actualCost,
                response.usage().promptTokens(),
                response.usage().completionTokens());

        creditService.deductCredits(user, apiKey, actualCost);

        return actualCost;
    }

    /**
     * Calculate response time in milliseconds.
     */
    private int calculateResponseTime(long startTime) {
        return (int) (System.currentTimeMillis() - startTime);
    }

    /**
     * Save the conversation history to the database.
     */
    private void saveConversationHistory(
            ChatCompletionRequest request,
            ChatCompletionResponse response,
            User user,
            ApiKey apiKey,
            ModelProviderMapping mapping,
            double cost,
            int responseTimeMs) {

        conversationService.saveConversation(
                request,
                response,
                user,
                apiKey,
                mapping,
                cost,
                responseTimeMs
        );

        log.debug("Conversation saved successfully");
    }
}
