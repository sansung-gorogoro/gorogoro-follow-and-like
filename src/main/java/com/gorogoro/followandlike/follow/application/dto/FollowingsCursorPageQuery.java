package com.gorogoro.followandlike.follow.application.dto;

import java.util.Objects;

public record FollowingsCursorPageQuery(
        Long followerId,
        Long pageCursor,
        int pageSize
) {
    public FollowingsCursorPageQuery {

        final int MIN_PAGE_SIZE = 1;
        final int MAX_PAGE_SIZE = 100;

        Objects.requireNonNull(followerId, "followerId");
        if (followerId < 0) {
            throw new IllegalArgumentException("followerId 는 양수여야 합니다. followerId = " + followerId);
        }

        if (pageCursor != null && pageCursor < 0) {
            throw new IllegalArgumentException("pageCursor는 음수일 수 없습니다. pageCursor = " + pageCursor);
        }

        if (pageSize < MIN_PAGE_SIZE || MAX_PAGE_SIZE < pageSize) {
            throw new IllegalArgumentException(
                    "pageSize 가 " + MIN_PAGE_SIZE +" 보다 작거나 " + MAX_PAGE_SIZE + " 보다 큽니다.");
        }
    }
}