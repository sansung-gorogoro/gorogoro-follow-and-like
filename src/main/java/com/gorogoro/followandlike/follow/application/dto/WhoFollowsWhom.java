package com.gorogoro.followandlike.follow.application.dto;

import java.util.Objects;

public record WhoFollowsWhom (
        Long followerId,
        Long followeeId
) {

    public WhoFollowsWhom {
        Objects.requireNonNull(followerId, "followerId");
        if (followerId <= 0) {
            throw new IllegalArgumentException("followerId 는 양수여야 합니다. followerId = " + followerId);
        }

        Objects.requireNonNull(followeeId, "followeeId");
        if (followeeId <= 0) {
            throw new IllegalArgumentException("followeeId 는 양수여야 합니다. followeeId = " + followeeId);
        }
    }
}
