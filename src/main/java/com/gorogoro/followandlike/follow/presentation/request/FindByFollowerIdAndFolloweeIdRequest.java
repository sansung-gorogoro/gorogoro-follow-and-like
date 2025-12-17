package com.gorogoro.followandlike.follow.presentation.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_FOLLOWEE_ID_IS_NULL;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_FOLLOWEE_ID_OUT_OF_RANGE;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_FOLLOWER_ID_IS_NULL;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_FOLLOWER_ID_OUT_OF_RANGE;

public record FindByFollowerIdAndFolloweeIdRequest(
        @NotNull(message = ERR_MSG_FOLLOWER_ID_IS_NULL)
        @Positive(message = ERR_MSG_FOLLOWER_ID_OUT_OF_RANGE)
        Long followerId,

        @NotNull(message = ERR_MSG_FOLLOWEE_ID_IS_NULL)
        @Positive(message = ERR_MSG_FOLLOWEE_ID_OUT_OF_RANGE)
        Long followeeId
) {
}
