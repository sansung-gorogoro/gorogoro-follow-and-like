package com.gorogoro.followandlike.follow.domain.exception;

import com.gorogoro.followandlike.common.domain.exception.BaseDomainException;

public class FollowException extends BaseDomainException {

    public FollowException(FollowErrorCode code) {
        super(code);
    }
}
