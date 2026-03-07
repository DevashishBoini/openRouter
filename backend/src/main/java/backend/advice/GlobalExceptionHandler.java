package backend.advice;

import backend.dto.ErrorResponse;
import backend.exception.ResourceNotFoundException;
import backend.exception.EmailAlreadyExistsException;
import backend.exception.InvalidCredentialsException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * Resource not found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFound(ResourceNotFoundException ex) {

        logger.warn("Resource not found: {}", ex.getMessage());

        return new ErrorResponse(
                false,
                ex.getMessage(),
                null
        );
    }


    /**
     * Email already exists
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExists(EmailAlreadyExistsException ex) {

        logger.warn("Email already exists: {}", ex.getMessage());

        return new ErrorResponse(
                false,
                ex.getMessage(),
                null
        );
    }


    /**
     * Invalid login credentials
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidCredentials(InvalidCredentialsException ex) {

        logger.warn("Invalid credentials attempt");

        return new ErrorResponse(
                false,
                ex.getMessage(),
                null
        );
    }


    /**
     * Validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        logger.warn("Validation error: {}", errors);

        return new ErrorResponse(
                false,
                "Validation failed",
                errors
        );
    }


    /**
     * Invalid path parameter type (e.g., invalid UUID format)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

        String parameterName = ex.getName();
        String invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        logger.warn("Type mismatch error: parameter='{}', value='{}', expectedType='{}'",
                parameterName, invalidValue, expectedType);

        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected a valid %s format.",
                invalidValue, parameterName, expectedType
        );

        return new ErrorResponse(
                false,
                message,
                null
        );
    }


    /**
     * Database errors
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ErrorResponse handleDatabaseError(DataAccessException ex) {

        logger.error("Database error occurred: {}", ex.getMessage(), ex);

        return new ErrorResponse(
                false,
                "Service temporarily unavailable",
                null
        );
    }


    /**
     * Catch-all exception handler
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {

        logger.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        return new ErrorResponse(
                false,
                "An unexpected error occurred. Please try again later.",
                null
        );
    }


    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoHandlerFound(NoHandlerFoundException ex) {

        logger.warn("Unknown route requested: {}", ex.getRequestURL());

        return new ErrorResponse(
                false,
                "API endpoint not found",
                ex.getRequestURL()
        );
    }

    /**
     * Invalid JSON or illegal datatype
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRequestBody(HttpMessageNotReadableException ex) {

        logger.warn("Invalid request body: {}", ex.getMessage());

        return new ErrorResponse(
                false,
                "Invalid request body or datatype",
                null
        );
    }
}