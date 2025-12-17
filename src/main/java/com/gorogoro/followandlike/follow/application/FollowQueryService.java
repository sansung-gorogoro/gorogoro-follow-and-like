package com.gorogoro.followandlike.follow.application;

import com.gorogoro.followandlike.follow.application.dto.FollowersCursorPageQuery;
import com.gorogoro.followandlike.follow.application.dto.FollowingsCursorPageQuery;
import com.gorogoro.followandlike.follow.application.dto.FollowQueryResult;
import com.gorogoro.followandlike.follow.application.dto.RequiredNonNegativeId;
import com.gorogoro.followandlike.follow.application.dto.WhoFollowsWhom;
import com.gorogoro.followandlike.follow.domain.exception.FollowException;
import com.gorogoro.followandlike.common.application.dto.CursorPageResult;
import com.gorogoro.followandlike.follow.domain.model.Follow;
import com.gorogoro.followandlike.follow.domain.repository.FollowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.gorogoro.followandlike.follow.domain.exception.FollowErrorCode.FOLLOW_NOT_FOUND;

@Service
@Transactional(readOnly = true)
public class FollowQueryService {

    private final FollowRepository followRepository;

    public FollowQueryService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public FollowQueryResult findById(RequiredNonNegativeId requiredNonNegativeId) {
        return FollowQueryResult.from(
                followRepository.findById(requiredNonNegativeId.val())
                        .orElseThrow(() -> new FollowException(FOLLOW_NOT_FOUND))
        );
    }

    public FollowQueryResult findByFollowerIdAndFolloweeId(WhoFollowsWhom whoFollowsWhom) {
        Optional<Follow> optionalFollow = followRepository.findByFollowerIdAndFolloweeId(
                whoFollowsWhom.followerId(),
                whoFollowsWhom.followeeId()
        );
        return optionalFollow.map(FollowQueryResult::from).orElse(null);
    }

    public long countByFollowerId(RequiredNonNegativeId followerId) {
        return followRepository.countByFollowerId(followerId.val());
    }

    public long countByFolloweeId(RequiredNonNegativeId followeeId) {
        return followRepository.countByFolloweeId(followeeId.val());
    }

    public CursorPageResult<FollowQueryResult> findFollowings(FollowingsCursorPageQuery followingsCursorPageQuery) {

        CursorPageResult<Follow> followings = followRepository.getFollowings(
                followingsCursorPageQuery.followerId(),
                followingsCursorPageQuery.pageCursor(),
                followingsCursorPageQuery.fetchSize()
        );

        List<FollowQueryResult> responseContent = followings.content().stream()
                .map(FollowQueryResult::from)
                .toList();

        return new CursorPageResult<>(
                responseContent, followings.nextCursor(), followings.hasNext());
    }

    public CursorPageResult<FollowQueryResult> findFollowers(FollowersCursorPageQuery followersCursorPageQuery) {

        CursorPageResult<Follow> followings = followRepository.getFollowers(
                followersCursorPageQuery.followeeId(),
                followersCursorPageQuery.pageCursor(),
                followersCursorPageQuery.fetchSize()
        );

        List<FollowQueryResult> responseContent = followings.content().stream()
                .map(FollowQueryResult::from)
                .toList();

        return new CursorPageResult<>(
                responseContent, followings.nextCursor(), followings.hasNext());
    }
}
