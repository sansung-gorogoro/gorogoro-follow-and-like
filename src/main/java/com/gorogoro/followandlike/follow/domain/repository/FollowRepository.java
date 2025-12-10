package com.gorogoro.followandlike.follow.domain.repository;

import com.gorogoro.followandlike.follow.domain.model.Follow;
import com.gorogoro.followandlike.follow.domain.model.dto.CursorBasedPaginatedResult;
import lombok.NonNull;

import java.util.Optional;

public interface FollowRepository {

    Optional<Follow> findById(@NonNull Long id);

    Optional<Follow> findByFollowerIdAndFolloweeId(@NonNull Long followerId, @NonNull Long followeeId);

    long countByFollowerId(@NonNull Long followerId);

    long countByFolloweeId(@NonNull Long followeeId);

    CursorBasedPaginatedResult<Follow> getFollowings(@NonNull Long followerId, Long pageCursor, int fetchSize);

    CursorBasedPaginatedResult<Follow> getFollowers(@NonNull Long followeeId, Long pageCursor, int fetchSize);

    Follow save(@NonNull Follow follow);

    void deleteByFollowerIdAndFolloweeId(@NonNull Long followerId, @NonNull Long followeeId);
}
