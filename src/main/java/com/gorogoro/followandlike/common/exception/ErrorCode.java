package com.gorogoro.followandlike.common.exception;

import org.springframework.http.HttpStatusCode;

public interface ErrorCode {
    HttpStatusCode getHttpStatusCode();
    String getHttpStatusMessage();
    String getDomainErrorMessage();
}
