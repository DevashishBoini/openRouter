package gateway.service;

import gateway.dbModel.ApiKey;
import gateway.dbModel.ModelProviderMapping;
import gateway.dbModel.User;
import gateway.exception.InsufficientCreditsException;
import gateway.repository.ApiKeyRepository;
import gateway.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service responsible for managing user credits and API key usage tracking.
 *
 * <p>Handles credit checking before requests, deduction after successful
 * LLM calls, and updating API key metadata.</p>
 *
 * <p><b>Cost Calculation:</b></p>
 * <ul>
 *   <li>Database stores cost per 1M tokens</li>
 *   <li>Commission multiplier applied to account for platform margin</li>
 *   <li>Formula: ((inputTokens/1M * inputCost) + (outputTokens/1M * outputCost)) * multiplier</li>
 * </ul>
 */
@Service
@Slf4j
public class CreditService {

    private static final double TOKENS_PER_MILLION = 1_000_000.0;

    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final double commissionMultiplier;

    public CreditService(
            UserRepository userRepository,
            ApiKeyRepository apiKeyRepository,
            @Value("${gateway.pricing.commission-multiplier:1.05}") double commissionMultiplier) {
        this.userRepository = userRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.commissionMultiplier = commissionMultiplier;

        log.info("CreditService initialized with commission multiplier: {}", commissionMultiplier);
    }

    /**
     * Checks if the user has sufficient credits for the estimated request cost.
     *
     * @param user the user making the request
     * @param estimatedCost estimated cost in credits
     * @throws InsufficientCreditsException if user has insufficient credits
     */
    public void checkSufficientCredits(User user, double estimatedCost) {

        if (user.getCredits() < estimatedCost) {
            log.warn("Insufficient credits: userId={}, available={}, required={}",
                    user.getId(), user.getCredits(), estimatedCost);
            throw new InsufficientCreditsException(
                    String.format("Insufficient credits. Available: %.2f, Required: ~%.2f",
                            user.getCredits(), estimatedCost)
            );
        }

        log.info("Credit check passed: userId={}, available={}, required={}",
                user.getId(), user.getCredits(), estimatedCost);
    }

    /**
     * Deducts credits from the user and updates API key usage after a successful LLM call.
     *
     * @param user the user to deduct credits from
     * @param apiKey the API key used for the request
     * @param cost the actual cost calculated from token usage
     */
    @Transactional
    public void deductCredits(User user, ApiKey apiKey, double cost) {

        // Deduct from user credits
        user.setCredits(user.getCredits() - cost);
        userRepository.save(user);

        // Update API key metadata
        apiKey.setCreditsConsumed(apiKey.getCreditsConsumed() + cost);
        apiKey.setLastUsed(LocalDateTime.now());
        apiKeyRepository.save(apiKey);

        log.info("Credits deducted: userId={}, apiKeyId={}, cost={}, remainingCredits={}",
                user.getId(), apiKey.getId(), cost, user.getCredits());
    }

    /**
     * Calculates the cost of a request based on token usage and pricing.
     *
     * <p><b>Calculation:</b></p>
     * <pre>
     * baseCost = (inputTokens / 1,000,000 * inputTokenCost) + (outputTokens / 1,000,000 * outputTokenCost)
     * finalCost = baseCost * commissionMultiplier
     * </pre>
     *
     * <p><b>Notes:</b></p>
     * <ul>
     *   <li>Database stores cost per 1M tokens (e.g., $0.03 per 1M input tokens)</li>
     *   <li>Commission multiplier accounts for platform margin (default: 1.05 = 5% markup)</li>
     *   <li>TODO: Make commission multiplier model-specific (currently same for all models)</li>
     * </ul>
     *
     * @param inputTokens number of tokens in the request
     * @param outputTokens number of tokens in the response
     * @param mapping the model-provider mapping with pricing per 1M tokens
     * @return the calculated cost in credits
     */
    public double calculateCost(int inputTokens, int outputTokens, ModelProviderMapping mapping) {

        // Calculate base cost (pricing is per 1M tokens)
        double inputCost = (inputTokens / TOKENS_PER_MILLION) * mapping.getInputTokenCost();
        double outputCost = (outputTokens / TOKENS_PER_MILLION) * mapping.getOutputTokenCost();
        double baseCost = inputCost + outputCost;

        // Apply commission multiplier (platform margin)
        // TODO: Make this model-specific via database (e.g., model_provider_mappings.commission_multiplier)
        // For now, using global configuration for all models
        double finalCost = baseCost * commissionMultiplier;

        log.debug("Cost calculated: inputTokens={}, outputTokens={}, baseCost={}, multiplier={}, finalCost={}",
                inputTokens, outputTokens, baseCost, commissionMultiplier, finalCost);

        return finalCost;
    }
}
