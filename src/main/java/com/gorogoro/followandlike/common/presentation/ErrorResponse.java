package com.gorogoro.followandlike.common.presentation;

import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        List<FieldError> errors
) {
    public record FieldError(
            String field,
            String reason
    ) {
    }

    public static ErrorResponse of(String code, String message, List<FieldError> errors) {
        return new ErrorResponse(code, message, errors);
    }
}
