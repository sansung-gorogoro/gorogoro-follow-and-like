package com.gorogoro.followandlike.follow.domain.exception;

import lombok.Getter;

@Getter
public enum FollowErrorCode {

    // CREATION_ERROR: 0xx
    ID_IS_NULL("001", "id 가 null 입니다."),
    FOLLOWER_ID_IS_NULL("002", "followerId 가 null 입니다."),
    FOLLOWER_ID_IS_NEGATIVE("003", "followerId 가 음수입니다."),
    FOLLOWEE_ID_IS_NULL("004", "followeeId 가 null 입니다."),
    FOLLOWEE_ID_IS_NEGATIVE("005", "followeeId 가 음수입니다."),
    FOLLOWED_ONESELF("006", "자기자신을 팔로우할 수 없습니다. followerId 와 followeeId 가 같습니다.");

    private final String code;
    private final String message;

    FollowErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
