package com.gorogoro.followandlike.common.exception;

import org.springframework.http.HttpStatusCode;

public interface ErrorCode {
    HttpStatusCode getHttpStatus();
    String getHttpStatusMessage();
    String getDomainErrorMessage();
}
