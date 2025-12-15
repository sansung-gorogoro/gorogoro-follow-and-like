package com.gorogoro.followandlike.follow.application.dto;

import java.util.Objects;

public record FollowersCursorPageRequest(
        Long followeeId,
        Long pageCursor,
        int fetchSize
) {
    public FollowersCursorPageRequest {

        final int MIN_FETCH_SIZE = 1;
        final int MAX_FETCH_SIZE = 100;

        Objects.requireNonNull(followeeId, "followeeId");
        if (followeeId < 0) {
            throw new IllegalArgumentException("followeeId 는 양수여야 합니다. followeeId = " + followeeId);
        }

        if (pageCursor < 0) {
            throw new IllegalArgumentException("pageCursor는 음수일 수 없습니다. pageCursor = " + pageCursor);
        }

        if (fetchSize < MIN_FETCH_SIZE || MAX_FETCH_SIZE < fetchSize) {
            throw new IllegalArgumentException(
                    "fetchSize 가 " + MIN_FETCH_SIZE +" 보다 작거나 " + MAX_FETCH_SIZE + " 보다 큽니다.");
        }
    }
}