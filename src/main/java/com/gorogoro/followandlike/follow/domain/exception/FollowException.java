package com.gorogoro.followandlike.follow.domain.exception;

import com.gorogoro.followandlike.common.exception.BaseDomainException;
import lombok.Getter;

public class FollowException extends BaseDomainException {

    @Getter
    private final FollowErrorCode code;

    public FollowException(FollowErrorCode code) {
        super(code.getMessage());
        this.code = code;
    }
}
