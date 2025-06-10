package com.example.assignment.global.customizer;

import com.example.assignment.global.annotation.ApiErrorResponse;
import com.example.assignment.global.annotation.ApiErrorResponses;
import com.example.assignment.global.dto.response.ErrorResponse;
import com.example.assignment.global.exception.ExceptionType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;


import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Component
public class ApiErrorResponseCustomizer implements OperationCustomizer {
    private final Schema<ErrorResponse> errorResponseSchema = ModelConverters.getInstance()
            .readAllAsResolvedSchema(ErrorResponse.class).schema;
    private final Schema<ErrorResponse.ErrorDetail> errorDetailSchema = ModelConverters.getInstance()
            .readAllAsResolvedSchema(ErrorResponse.ErrorDetail.class).schema;

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiResponses apiResponses = operation.getResponses();
        ApiErrorResponse apiErrorResponse = handlerMethod.getMethodAnnotation(ApiErrorResponse.class);
        ApiErrorResponses apiErrorResponses = handlerMethod.getMethodAnnotation(ApiErrorResponses.class);

        if (apiErrorResponse != null) {
            generateApiErrorResponseCode(apiResponses, apiErrorResponse);
        }
        if (apiErrorResponses != null) {
            generateApiErrorResponsesCode(apiResponses, apiErrorResponses);
        }
        return operation;
    }

    private void generateApiErrorResponseCode(ApiResponses apiResponses, ApiErrorResponse apiErrorResponse) {
        ExceptionType type = apiErrorResponse.value();
        apiResponses.put(type.name(), convertErrorResponse(type));
    }

    private void generateApiErrorResponsesCode(ApiResponses apiResponses, ApiErrorResponses apiErrorResponses) {
        Set<ExceptionType> types = Arrays.stream(apiErrorResponses.value())
                .collect(Collectors.toSet());

        putApiErrorResponseWithGroupStatus(apiResponses, types);
    }

    private void putApiErrorResponseWithGroupStatus(ApiResponses apiResponses, Set<ExceptionType> types) {
        Map<HttpStatus, List<ExceptionType>> map = types.stream()
                .collect(Collectors.groupingBy(ExceptionType::getHttpStatus));

        map.entrySet()
                .stream()
                .forEach(entry ->
                        putApiErrorResponseCode(apiResponses, entry.getKey(), entry.getValue())
                );
    }

    private void putApiErrorResponseCode(ApiResponses apiResponses, HttpStatus status, List<ExceptionType> types) {
        apiResponses.put(String.valueOf(status.value()), convertErrorResponses(types));
    }

    private ApiResponse convertErrorResponse(ExceptionType exceptionType) {
        MediaType mediaType = new MediaType().schema(errorResponseSchema);
        mediaType.setSchema(errorDetailSchema);

        mediaType.addExamples(
                exceptionType.name(),
                new Example().value(ErrorResponse.of(exceptionType,exceptionType.getMessage()))
        );

        return new ApiResponse()
                .description(exceptionType.getHttpStatus().getReasonPhrase())
                .content(new Content().addMediaType(APPLICATION_JSON_VALUE, mediaType));
    }

    private ApiResponse convertErrorResponses(List<ExceptionType> exceptionTypes) {
        MediaType mediaType = new MediaType().schema(errorResponseSchema);
        mediaType.setSchema(errorDetailSchema);

        exceptionTypes.forEach(type ->
                mediaType.addExamples(type.name(), new Example().value(ErrorResponse.of(type, type.getMessage())))
        );

        return new ApiResponse()
                .description(exceptionTypes.get(0).getHttpStatus().getReasonPhrase())
                .content(new Content().addMediaType(APPLICATION_JSON_VALUE, mediaType));
    }
}
