package com.gorogoro.followandlike.follow.presentation.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gorogoro.followandlike.follow.application.dto.FollowQueryResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public record FollowResponse(
        Long id,
        Long followerId,
        Long followeeId,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssX", timezone = "UTC")
        Instant createdAt
) {
    public static FollowResponse from(FollowQueryResult result) {
        return new FollowResponse(
                result.id(),
                result.followerId(),
                result.followeeId(),
                result.createdAt().truncatedTo(ChronoUnit.SECONDS)
        );
    }
}
