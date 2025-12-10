package com.gorogoro.followandlike.common.exception;

public interface ErrorCode {
    int getHttpStatus();
    String getHttpStatusMessage();
    String getDomainErrorMessage();
}
