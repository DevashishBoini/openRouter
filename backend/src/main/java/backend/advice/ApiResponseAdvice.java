package backend.advice;

import backend.annotation.SuccessMessage;
import backend.dto.SuccessResponse;
import backend.dto.BaseApiResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        if (body instanceof BaseApiResponse) {
            return body;
        }

        SuccessMessage annotation =
                returnType.getMethodAnnotation(SuccessMessage.class);

        String message = "Success";

        if (annotation != null) {
            message = annotation.value();
        }

        return new SuccessResponse<>(
                true,
                message,
                body
        );
    }
}