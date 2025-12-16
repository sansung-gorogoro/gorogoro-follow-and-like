package com.gorogoro.followandlike.common.presentation;

import com.gorogoro.followandlike.common.domain.exception.BaseDomainException;
import com.gorogoro.followandlike.common.domain.exception.ErrorCode;
import com.gorogoro.followandlike.common.presentation.ErrorResponse.FieldError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final String MSG_INTERNAL_SERVER_ERROR = "Internal Server Error";

    // Http exception handlers --------------------

    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        final String ERROR_CODE = "UNSUPPORTED_HTTP_METHOD";
        final String ERROR_MSG = "지원되지 않는 HTTP 메서드, supported: "
                + (e.getSupportedHttpMethods() == null ? null : e.getSupportedHttpMethods().toString())
                + ", given: "
                + e.getMethod();

        ErrorResponse body = ErrorResponse.of(ERROR_CODE, ERROR_MSG);

        log.warn("{}: {}", ERROR_CODE, ERROR_MSG, e);

        return ResponseEntity
                .badRequest()
                .body(body);
    }

    @ResponseBody
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException e) {
        final String ERROR_CODE = "MISSING_HTTP_HEADER";
        final String ERROR_MSG = "헤더 누락, required: " + e.getHeaderName();

        ErrorResponse body = ErrorResponse.of(ERROR_CODE, ERROR_MSG);

        log.warn("{}: {}", ERROR_CODE, ERROR_MSG, e);

        return ResponseEntity
                .badRequest()
                .body(body);
    }

    // Validation exception handlers --------------------

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final String ERROR_CODE = "VALIDATION_FAILED";
        final String ERROR_MSG = "유효하지 않은 요청 파라미터";

        List<FieldError> fieldErrors = getFieldErrors(e);
        ErrorResponse body = ErrorResponse.of(ERROR_CODE, ERROR_MSG, fieldErrors);

        log.debug("{}: {}, \n 상세 원인: {}", ERROR_CODE, ERROR_MSG, fieldErrors, e);

        return ResponseEntity.badRequest().body(body);
    }

    @ResponseBody
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        final String ERROR_CODE = "VALIDATION_FAILED";
        final String ERROR_MSG = "유효하지 않은 요청 파라미터";

        List<FieldError> fieldErrors = getFieldErrors(e);
        ErrorResponse body = ErrorResponse.of(ERROR_CODE, ERROR_MSG, fieldErrors);

        log.debug("{}: {}, \n 상세 원인: {}", ERROR_CODE, ERROR_MSG, fieldErrors, e);

        return ResponseEntity.badRequest().body(body);
    }

    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {
        final String ERROR_CODE = "VALIDATION_FAILED";
        final String ERROR_MSG = "유효하지 않은 요청 파라미터";

        List<FieldError> fieldErrors = getFieldErrors(e);
        ErrorResponse body = ErrorResponse.of(ERROR_CODE, ERROR_MSG, fieldErrors);

        log.debug("{}: {}, \n 상세 원인: {}", ERROR_CODE, ERROR_MSG, fieldErrors, e);

        return ResponseEntity.badRequest().body(body);
    }

    @ResponseBody
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidationException(HandlerMethodValidationException e) {
        final String ERROR_CODE = "VALIDATION_FAILED";
        final String ERROR_MSG = "유효하지 않은 요청 파라미터";

        List<FieldError> fieldErrors = getFieldErrors(e);
        ErrorResponse body = ErrorResponse.of(ERROR_CODE, ERROR_MSG, fieldErrors);

        log.debug("{}: {}, \n 상세 원인: {}", ERROR_CODE, ERROR_MSG, fieldErrors, e);

        return ResponseEntity.badRequest().body(body);
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        final String ERROR_CODE = "VALIDATION_FAILED";
        final String ERROR_MSG = "유효하지 않은 요청 파라미터";

        List<FieldError> fieldErrors = getFieldErrors(e);
        ErrorResponse body = ErrorResponse.of(ERROR_CODE, ERROR_MSG, fieldErrors);

        log.debug("{}: {}, \n 상세 원인: {}", ERROR_CODE, ERROR_MSG, fieldErrors, e);

        return ResponseEntity.badRequest().body(body);
    }

    // Domain exception handlers --------------------

    @ResponseBody
    @ExceptionHandler(BaseDomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(BaseDomainException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse body = ErrorResponse.of(errorCode.getCode(), errorCode.getDomainErrorMessage());

        log.warn("도메인 예외 - {}: {}", errorCode.getName(), errorCode.getDomainErrorMessage(), e);

        return ResponseEntity.badRequest().body(body);
    }

    // DB related exception handlers --------------------

    @ResponseBody
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccessException(DataAccessException e) {
        final String ERROR_CODE = "DATA_ACCESS_FAILED";
        final String ERROR_MSG = "예기치 못한 데이터 접근 실패";

        ErrorResponse body = ErrorResponse.of(ERROR_CODE, ERROR_MSG);

        log.debug("{}: {}", ERROR_CODE, ERROR_MSG, e);

        return ResponseEntity.internalServerError().body(body);
    }

    // Default handler --------------------

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        final String ERROR_CODE = "UNEXPECTED_SERVER_ERROR";
        final String ERROR_MSG = "예기치 못한 서버 예외";

        ErrorResponse body = ErrorResponse.of(ERROR_CODE, ERROR_MSG);

        log.debug("{}: {}", ERROR_CODE, ERROR_MSG, e);

        return ResponseEntity.internalServerError().body(body);
    }

    // Helpers --------------------

    private List<FieldError> getFieldErrors(BindException e) {
        return e.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldError(
                        error.getField(),
                        error.getDefaultMessage(),
                        error.getRejectedValue()
                ))
                .toList();
    }

    private List<FieldError> getFieldErrors(ConstraintViolationException e) {
        return e.getConstraintViolations().stream()
                .map(violation -> new FieldError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()
                ))
                .toList();
    }

    private List<FieldError> getFieldErrors(HandlerMethodValidationException e) {
        return e.getParameterValidationResults().stream()
                .map(result -> new FieldError(
                        result.getMethodParameter().getParameterName(),
                        result.getResolvableErrors()
                                .stream()
                                .map(MessageSourceResolvable::getDefaultMessage)
                                .collect(Collectors.joining(", ")),
                        result.getArgument() == null ? null : result.getArgument().toString()
                ))
                .toList();
    }

    private List<FieldError> getFieldErrors(MethodArgumentTypeMismatchException e) {
        return List.of(
                new FieldError(
                        e.getName(),
                        "expected type: " + (e.getRequiredType() == null ? null : e.getRequiredType().getSimpleName()),
                        e.getValue() == null ? null : e.getValue().toString()
                )
        );
    }
}
