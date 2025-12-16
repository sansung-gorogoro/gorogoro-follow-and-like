package com.gorogoro.followandlike.common.domain.exception;

import org.springframework.http.HttpStatusCode;

public interface ErrorCode {
    HttpStatusCode getHttpStatusCode();
    String getDomainErrorMessage();
    String getCode();
}
