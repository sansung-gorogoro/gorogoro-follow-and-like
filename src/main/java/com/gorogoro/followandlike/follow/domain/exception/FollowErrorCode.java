package com.gorogoro.followandlike.follow.domain.exception;

import com.gorogoro.followandlike.common.domain.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum FollowErrorCode implements ErrorCode {

    FOLLOWER_ID_IS_NULL(HttpStatus.BAD_REQUEST, "followerId 가 null 입니다.", "FOL-001"),
    FOLLOWER_ID_IS_NEGATIVE(HttpStatus.BAD_REQUEST, "followerId 가 음수입니다.", "FOL-002"),
    FOLLOWEE_ID_IS_NULL(HttpStatus.BAD_REQUEST, "followeeId 가 null 입니다.", "FOL-003"),
    FOLLOWEE_ID_IS_NEGATIVE(HttpStatus.BAD_REQUEST, "followeeId 가 음수입니다.", "FOL-004"),
    FOLLOWED_ONESELF(HttpStatus.BAD_REQUEST, "자기자신을 팔로우할 수 없습니다. followerId 와 followeeId 가 같습니다.", "FOL-005"),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우를 찾을 수 없습니다.", "FOL-006")
    ;

    private final HttpStatus httpStatus;
    private final String domainErrorMessage;
    private final String code;

    FollowErrorCode(HttpStatus httpStatus, String domainErrorMessage, String code) {
        this.httpStatus = httpStatus;
        this.domainErrorMessage = domainErrorMessage;
        this.code = code;
    }

    @Override
    public HttpStatus getHttpStatusCode() {
        return httpStatus;
    }

    @Override
    public String getDomainErrorMessage() {
        return domainErrorMessage;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return this.name();
    }


}
