package gateway.advice;

import gateway.dto.ErrorResponse;
import gateway.exception.InsufficientCreditsException;
import gateway.exception.InvalidApiKeyException;
import gateway.exception.ModelNotFoundException;
import gateway.exception.ProviderException;
import gateway.exception.RateLimitExceededException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the gateway application.
 *
 * <p>Catches exceptions thrown by controllers and services and converts
 * them into appropriate HTTP error responses.</p>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Invalid API key or authentication failure
     */
    @ExceptionHandler(InvalidApiKeyException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidApiKey(InvalidApiKeyException ex) {

        log.warn("Invalid API key: {}", ex.getMessage());

        return new ErrorResponse(
                false,
                ex.getMessage(),
                null
        );
    }

    /**
     * Insufficient credits
     */
    @ExceptionHandler(InsufficientCreditsException.class)
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public ErrorResponse handleInsufficientCredits(InsufficientCreditsException ex) {

        log.warn("Insufficient credits: {}", ex.getMessage());

        return new ErrorResponse(
                false,
                ex.getMessage(),
                null
        );
    }

    /**
     * Rate limit exceeded
     */
    @ExceptionHandler(RateLimitExceededException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ErrorResponse handleRateLimitExceeded(RateLimitExceededException ex) {

        log.warn("Rate limit exceeded: {}", ex.getMessage());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("limit", ex.getLimit());
        metadata.put("retryAfterSeconds", ex.getRetryAfterSeconds());

        return new ErrorResponse(
                false,
                ex.getMessage(),
                metadata
        );
    }

    /**
     * Model not found
     */
    @ExceptionHandler(ModelNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleModelNotFound(ModelNotFoundException ex) {

        log.warn("Model not found: {}", ex.getMessage());

        return new ErrorResponse(
                false,
                ex.getMessage(),
                null
        );
    }

    /**
     * Provider error (OpenAI API failure)
     */
    @ExceptionHandler(ProviderException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorResponse handleProviderError(ProviderException ex) {

        log.error("Provider error: {}", ex.getMessage(), ex);

        return new ErrorResponse(
                false,
                "LLM provider error: " + ex.getMessage(),
                null
        );
    }

    /**
     * Validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        log.warn("Validation error: {}", errors);

        return new ErrorResponse(
                false,
                "Validation failed",
                errors
        );
    }

    /**
     * Catch-all exception handler
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {

        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        return new ErrorResponse(
                false,
                "An unexpected error occurred. Please try again later.",
                null
        );
    }
}
