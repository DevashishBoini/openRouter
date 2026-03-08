package gateway.config;

import gateway.annotation.CurrentApiKey;
import gateway.security.ApiKeyAuthentication;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Custom argument resolver for @CurrentApiKey annotation.
 *
 * <p>Extracts the ApiKeyAuthentication from the SecurityContext and injects it
 * into controller method parameters annotated with @CurrentApiKey.</p>
 */
@Component
public class ApiKeyAuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentApiKey.class)
                && parameter.getParameterType().equals(ApiKeyAuthentication.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof ApiKeyAuthentication) {
            return authentication;
        }

        return null;
    }
}
