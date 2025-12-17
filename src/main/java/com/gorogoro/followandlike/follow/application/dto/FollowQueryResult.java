package com.gorogoro.followandlike.follow.application.dto;

import com.gorogoro.followandlike.follow.domain.model.Follow;

import java.time.Instant;

public record FollowQueryResult(
        Long id,
        Long followerId,
        Long followeeId,
        Instant createdAt
) {

    public static FollowQueryResult from(Follow follow) {
        return new FollowQueryResult(follow.getId(), follow.getFollowerId(), follow.getFolloweeId(), follow.getCreatedAt());
    }
}
