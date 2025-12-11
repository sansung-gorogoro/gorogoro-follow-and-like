package com.gorogoro.followandlike.follow.domain.repository;

import com.gorogoro.followandlike.follow.domain.model.Follow;
import com.gorogoro.followandlike.follow.application.dto.CursorBasedPaginatedResult;

import java.util.Optional;

public interface FollowRepository {

    Optional<Follow> findById(Long id);

    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    long countByFollowerId(Long followerId);

    long countByFolloweeId(Long followeeId);

    CursorBasedPaginatedResult<Follow> getFollowings(Long followerId, Long pageCursor, int fetchSize);

    CursorBasedPaginatedResult<Follow> getFollowers(Long followeeId, Long pageCursor, int fetchSize);

    Follow save(Follow follow);

    void delete(Follow follow);

    void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
}
