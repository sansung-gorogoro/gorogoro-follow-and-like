package com.gorogoro.followandlike.common.presentation;

public record ErrorResponse(
        String code,
        String message,
        FieldError[] errors
) {
    public record FieldError(
            String field,
            String reason
    ) {
    }

    public static ErrorResponse of(String code, String message, FieldError[] errors) {
        return new ErrorResponse(code, message, errors);
    }
}
