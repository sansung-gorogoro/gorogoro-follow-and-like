package com.gorogoro.followandlike.common.domain.exception;

public class BaseDomainException extends RuntimeException {

    private final ErrorCode errorCode;

    public BaseDomainException(ErrorCode errorCode) {
        super(errorCode.getDomainErrorMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
