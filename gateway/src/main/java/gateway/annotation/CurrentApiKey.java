package gateway.annotation;

import java.lang.annotation.*;

/**
 * Annotation to inject the currently authenticated ApiKeyAuthentication
 * into controller method parameters.
 *
 * <p>Usage example:</p>
 * <pre>
 * {@code @PostMapping("/chat/completions")}
 * public ChatCompletionResponse chat(@CurrentApiKey ApiKeyAuthentication auth) {
 *     User user = auth.getUser();
 *     ApiKey apiKey = auth.getApiKey();
 *     // ...
 * }
 * </pre>
 *
 * <p>This annotation uses a custom argument resolver to extract the
 * ApiKeyAuthentication from the SecurityContext.</p>
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentApiKey {
}
