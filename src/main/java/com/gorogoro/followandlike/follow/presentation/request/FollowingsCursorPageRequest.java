package com.gorogoro.followandlike.follow.presentation.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_FOLLOWER_ID_IS_NULL;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_FOLLOWER_ID_OUT_OF_RANGE;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_PAGE_CURSOR_OUT_OF_RANGE;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_PAGE_SIZE_TOO_LARGE;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.ERR_MSG_PAGE_SIZE_TOO_SMALL;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.MAX_PAGE_SIZE;
import static com.gorogoro.followandlike.follow.presentation.FollowHttpConstants.MIN_PAGE_SIZE;

public record FollowingsCursorPageRequest(
        @NotNull(message = ERR_MSG_FOLLOWER_ID_IS_NULL)
        @Positive(message = ERR_MSG_FOLLOWER_ID_OUT_OF_RANGE)
        Long followerId,

        @PositiveOrZero(message = ERR_MSG_PAGE_CURSOR_OUT_OF_RANGE)
        Long pageCursor,

        @NotNull
        @Min(value = MIN_PAGE_SIZE, message = ERR_MSG_PAGE_SIZE_TOO_SMALL)
        @Max(value = MAX_PAGE_SIZE, message = ERR_MSG_PAGE_SIZE_TOO_LARGE)
        Integer pageSize
) {
}
