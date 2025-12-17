package com.gorogoro.followandlike.follow.application.dto;

import java.util.Objects;

public record FollowersCursorPageQuery(
        Long followeeId,
        Long pageCursor,
        int pageSize
) {
    public FollowersCursorPageQuery {

        final int MIN_PAGE_SIZE = 1;
        final int MAX_PAGE_SIZE = 100;

        Objects.requireNonNull(followeeId, "followeeId");
        if (followeeId < 0) {
            throw new IllegalArgumentException("followeeId 는 양수여야 합니다. followeeId = " + followeeId);
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