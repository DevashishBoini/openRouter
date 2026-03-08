package gateway.adapter.company;

import gateway.exception.ModelNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory for creating company adapters based on company name.
 *
 * <p>Uses the Factory Pattern to eliminate if-else chains and enable
 * dynamic adapter selection at runtime based on the model's company.</p>
 *
 * <p>Company adapters are automatically discovered via dependency injection
 * and registered in the factory map using their company name.</p>
 *
 * <p>Architecture flow:</p>
 * <pre>
 * Model (from DB) → Company → CompanyAdapter → ProviderAdapter → API Call
 * </pre>
 */
@Component
@Slf4j
public class CompanyAdapterFactory {

    private final Map<String, CompanyAdapter> adapters = new ConcurrentHashMap<>();

    /**
     * Constructor that auto-registers all company adapters found in the context.
     *
     * @param adapterList all CompanyAdapter beans (auto-injected by Spring)
     */
    public CompanyAdapterFactory(List<CompanyAdapter> adapterList) {
        for (CompanyAdapter adapter : adapterList) {
            String companyName = adapter.getCompanyName().toLowerCase();
            adapters.put(companyName, adapter);
            log.info("Registered company adapter: {}", companyName);
        }
    }

    /**
     * Gets the appropriate adapter for the given company name.
     *
     * @param companyName the company name (e.g., "openai", "anthropic", "google")
     * @return the corresponding CompanyAdapter
     * @throws ModelNotFoundException if no adapter is found for the company
     */
    public CompanyAdapter getAdapter(String companyName) {

        String normalizedName = companyName.toLowerCase().trim();

        CompanyAdapter adapter = adapters.get(normalizedName);

        if (adapter == null) {
            log.error("No adapter found for company: {}", companyName);
            throw new ModelNotFoundException("Unsupported company: " + companyName);
        }

        return adapter;
    }

    /**
     * Checks if an adapter exists for the given company.
     *
     * @param companyName the company name
     * @return true if adapter exists, false otherwise
     */
    public boolean hasAdapter(String companyName) {
        return adapters.containsKey(companyName.toLowerCase().trim());
    }
}
