package gateway.dto;

import gateway.exception.ModelNotFoundException;
import lombok.Getter;

import java.util.Set;

/**
 * Parses and represents a model slug to determine routing logic.
 *
 * <p><b>Required format: provider/model-name</b></p>
 *
 * <p>Supported formats:</p>
 * <ul>
 *   <li><b>openai/gpt-4</b> ✅</li>
 *   <li><b>anthropic/claude-3-haiku</b> ✅</li>
 *   <li><b>google/gemini-pro</b> ✅</li>
 *   <li><b>gpt-4</b> ❌ Error - prefix is mandatory</li>
 * </ul>
 *
 * <p><b>Note:</b> The provider hint from the slug is for future use. Currently, all requests
 * use the OpenAI provider regardless of the hint. In future versions, a "provider" field
 * will be added to the request body to explicitly specify the provider.</p>
 */
@Getter
public class ModelSlug {

    private static final String SEPARATOR = "/";

    // Recognized provider prefixes (for validation only, not used for routing yet)
    private static final Set<String> RECOGNIZED_PROVIDERS = Set.of("openai", "anthropic", "google");

    private final String providerHint;   // Hint from slug (not used for routing yet)
    private final String modelName;      // Model name for DB lookup
    private final String originalSlug;

    /**
     * Parses a model slug string.
     *
     * <p><b>Required format: provider/model-name</b></p>
     * <p>Example: "openai/gpt-4"</p>
     *
     * <p><b>Invalid formats:</b></p>
     * <ul>
     *   <li>"gpt-4" - Missing provider prefix</li>
     *   <li>"unknown/gpt-4" - Unrecognized provider</li>
     *   <li>"openai/" - Missing model name</li>
     * </ul>
     *
     * @param slug the model slug
     * @throws ModelNotFoundException if slug is invalid, missing prefix, or provider is unrecognized
     */
    public ModelSlug(String slug) {

        if (slug == null || slug.isBlank()) {
            throw new ModelNotFoundException("Model slug cannot be null or empty");
        }

        this.originalSlug = slug.trim();

        // Prefix is MANDATORY - no defaults
        if (!slug.contains(SEPARATOR)) {
            throw new ModelNotFoundException(
                    "Invalid model slug format. Expected 'provider/model-name', got: '" + slug + "'. " +
                    "Provider prefix is mandatory. Supported providers: " + RECOGNIZED_PROVIDERS
            );
        }

        // Parse: provider/model-name
        String[] parts = slug.split(SEPARATOR, 2);

        if (parts.length != 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new ModelNotFoundException(
                    "Invalid model slug format: '" + slug + "'. " +
                    "Expected 'provider/model-name' (e.g., 'openai/gpt-4')"
            );
        }

        String provider = parts[0].trim().toLowerCase();

        // Validate provider prefix is recognized
        if (!RECOGNIZED_PROVIDERS.contains(provider)) {
            throw new ModelNotFoundException(
                    "Unrecognized provider prefix: '" + provider + "'. " +
                    "Supported providers: " + RECOGNIZED_PROVIDERS
            );
        }

        this.providerHint = provider;
        this.modelName = parts[1].trim();
    }

    /**
     * Returns the model name for database queries.
     *
     * @return model name without provider prefix
     */
    public String getDatabaseSlug() {
        return modelName;
    }

    @Override
    public String toString() {
        return providerHint + SEPARATOR + modelName;
    }
}
