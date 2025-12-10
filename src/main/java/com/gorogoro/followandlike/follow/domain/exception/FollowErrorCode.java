package com.gorogoro.followandlike.follow.domain.exception;

import com.gorogoro.followandlike.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum FollowErrorCode implements ErrorCode {

    FOLLOWER_ID_IS_NULL(HttpStatus.BAD_REQUEST, "followerId 가 null 입니다."),
    FOLLOWER_ID_IS_NEGATIVE(HttpStatus.BAD_REQUEST, "followerId 가 음수입니다."),
    FOLLOWEE_ID_IS_NULL(HttpStatus.BAD_REQUEST, "followeeId 가 null 입니다."),
    FOLLOWEE_ID_IS_NEGATIVE(HttpStatus.BAD_REQUEST, "followeeId 가 음수입니다."),
    FOLLOWED_ONESELF(HttpStatus.BAD_REQUEST, "자기자신을 팔로우할 수 없습니다. followerId 와 followeeId 가 같습니다."),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "팔로우를 찾을 수 없습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String domainErrorMessage;

    FollowErrorCode(HttpStatus httpStatus, String domainErrorMessage) {
        this.httpStatus = httpStatus;
        this.domainErrorMessage = domainErrorMessage;
    }


    @Override
    public int getHttpStatus() {
        return httpStatus.value();
    }

    @Override
    public String getHttpStatusMessage() {
        return httpStatus.getReasonPhrase();
    }

    @Override
    public String getDomainErrorMessage() {
        return domainErrorMessage;
    }
}
