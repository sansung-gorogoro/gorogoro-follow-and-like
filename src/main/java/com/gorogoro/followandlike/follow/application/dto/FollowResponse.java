package com.gorogoro.followandlike.follow.application.dto;

import com.gorogoro.followandlike.follow.domain.model.Follow;

import java.time.Instant;

public record FollowResponse(
        Long id,
        Long followerId,
        Long followeeId,
        Instant createdAt
) {

    public static FollowResponse from(Follow follow) {
        return new FollowResponse(follow.getId(), follow.getFollowerId(), follow.getFolloweeId(), follow.getCreatedAt());
    }
}
