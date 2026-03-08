package gateway.adapter.provider;

import gateway.exception.ProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating provider adapters based on provider name.
 *
 * <p>Uses the Factory Pattern to eliminate if-else chains and enable
 * dynamic adapter selection at runtime. New providers can be added
 * simply by implementing {@link ProviderAdapter} and registering
 * as a Spring bean.</p>
 *
 * <p>Provider adapters are automatically discovered via dependency injection
 * and registered in the factory map using their provider name.</p>
 */
@Component
@Slf4j
public class ProviderAdapterFactory {

    private final Map<String, ProviderAdapter> adapters = new ConcurrentHashMap<>();

    /**
     * Constructor that auto-registers all provider adapters found in the context.
     *
     * @param adapterList all ProviderAdapter beans (auto-injected by Spring)
     */
    public ProviderAdapterFactory(List<ProviderAdapter> adapterList) {
        for (ProviderAdapter adapter : adapterList) {
            String providerName = adapter.getProviderName().toLowerCase();
            adapters.put(providerName, adapter);
            log.info("Registered provider adapter: {}", providerName);
        }
    }

    /**
     * Gets the appropriate adapter for the given provider name.
     *
     * @param providerName the provider name (e.g., "openai", "anthropic", "google")
     * @return the corresponding ProviderAdapter
     * @throws ProviderException if no adapter is found for the provider
     */
    public ProviderAdapter getAdapter(String providerName) {

        String normalizedName = providerName.toLowerCase().trim();

        ProviderAdapter adapter = adapters.get(normalizedName);

        if (adapter == null) {
            log.error("No adapter found for provider: {}", providerName);
            throw new ProviderException("Unsupported provider: " + providerName);
        }

        return adapter;
    }

    /**
     * Checks if an adapter exists for the given provider.
     *
     * @param providerName the provider name
     * @return true if adapter exists, false otherwise
     */
    public boolean hasAdapter(String providerName) {
        return adapters.containsKey(providerName.toLowerCase().trim());
    }
}
