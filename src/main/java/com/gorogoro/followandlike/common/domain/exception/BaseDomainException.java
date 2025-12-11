package com.gorogoro.followandlike.common.domain.exception;

public class BaseDomainException extends RuntimeException {

    private ErrorCode errorCode;

    public BaseDomainException(ErrorCode code) {
        super(code.getDomainErrorMessage());
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
