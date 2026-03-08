package gateway.controller;

import gateway.annotation.CurrentApiKey;
import gateway.dbModel.ApiKey;
import gateway.dbModel.User;
import gateway.dto.ChatCompletionRequest;
import gateway.dto.ChatCompletionResponse;
import gateway.security.ApiKeyAuthentication;
import gateway.service.ChatCompletionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling LLM chat completion requests.
 *
 * <p>This controller is intentionally lightweight - it only handles HTTP concerns
 * (request/response, validation, authentication extraction) and delegates all
 * business logic to {@link ChatCompletionService}.</p>
 *
 * <p>Supported model slug formats:</p>
 * <ul>
 *   <li><b>openai/gpt-4</b> - OpenAI model</li>
 *   <li><b>anthropic/claude-3-haiku</b> - Anthropic model</li>
 *   <li><b>google/gemini-pro</b> - Google model</li>
 * </ul>
 *
 * <p><b>Architecture:</b></p>
 * <pre>
 * Request → Controller (this) → ChatCompletionService
 *                                      ↓
 *                               RouterService, CreditService, CompanyAdapter
 *                                      ↓
 *                               ProviderAdapter → LLM API
 * </pre>
 *
 * <p>Base URL: <code>/v1</code></p>
 */
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatCompletionService chatCompletionService;

    /**
     * OpenAI-compatible chat completions endpoint.
     *
     * <p>Processes chat completion requests by validating the request,
     * extracting authentication, and delegating to the service layer.</p>
     *
     * <p><b>Request Format:</b></p>
     * <pre>
     * POST /v1/chat/completions
     * Authorization: Bearer YOUR_API_KEY
     * Content-Type: application/json
     *
     * {
     *   "model": "openai/gpt-4",
     *   "messages": [
     *     {"role": "user", "content": "Hello!"}
     *   ],
     *   "temperature": 0.7,
     *   "max_tokens": 1000
     * }
     * </pre>
     *
     * @param apiKeyAuth the authenticated API key (auto-injected)
     * @param request the chat completion request body (auto-validated)
     * @return the chat completion response from the LLM provider
     */
    @PostMapping("/chat/completions")
    @ResponseStatus(HttpStatus.OK)
    public ChatCompletionResponse createChatCompletion(
            @CurrentApiKey ApiKeyAuthentication apiKeyAuth,
            @Valid @RequestBody ChatCompletionRequest request) {

        User user = apiKeyAuth.getUser();
        ApiKey apiKey = apiKeyAuth.getApiKey();

        log.info("Chat completion request received: userId={}, apiKeyId={}, model={}",
                user.getId(), apiKey.getId(), request.model());

        return chatCompletionService.processCompletion(request, user, apiKey);
    }
}
