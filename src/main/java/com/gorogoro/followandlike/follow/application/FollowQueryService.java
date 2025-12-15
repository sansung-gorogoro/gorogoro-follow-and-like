package com.gorogoro.followandlike.follow.application;

import com.gorogoro.followandlike.follow.application.dto.FollowersCursorPageRequest;
import com.gorogoro.followandlike.follow.application.dto.FollowingsCursorPageRequest;
import com.gorogoro.followandlike.follow.application.dto.FollowResponse;
import com.gorogoro.followandlike.follow.application.dto.RequiredPositiveId;
import com.gorogoro.followandlike.follow.application.dto.WhoFollowsWhom;
import com.gorogoro.followandlike.follow.domain.exception.FollowException;
import com.gorogoro.followandlike.follow.application.dto.CursorPageResponse;
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

    public FollowResponse findById(RequiredPositiveId requiredPositiveId) {
        return FollowResponse.from(
                followRepository.findById(requiredPositiveId.val())
                        .orElseThrow(() -> new FollowException(FOLLOW_NOT_FOUND))
        );
    }

    public FollowResponse findByFollowerIdAndFolloweeId(WhoFollowsWhom whoFollowsWhom) {
        Optional<Follow> optionalFollow = followRepository.findByFollowerIdAndFolloweeId(
                whoFollowsWhom.followerId(),
                whoFollowsWhom.followeeId()
        );
        return optionalFollow.map(FollowResponse::from).orElse(null);
    }

    public long countByFollowerId(RequiredPositiveId followerId) {
        return followRepository.countByFollowerId(followerId.val());
    }

    public long countByFolloweeId(RequiredPositiveId followeeId) {
        return followRepository.countByFolloweeId(followeeId.val());
    }

    public CursorPageResponse<FollowResponse> findFollowings(FollowingsCursorPageRequest followingsCursorPageRequest) {

        CursorPageResponse<Follow> followings = followRepository.getFollowings(
                followingsCursorPageRequest.followerId(),
                followingsCursorPageRequest.pageCursor(),
                followingsCursorPageRequest.fetchSize()
        );

        List<FollowResponse> responseContent = followings.content().stream()
                .map(FollowResponse::from)
                .toList();

        return new CursorPageResponse<>(
                responseContent, followings.nextCursor(), followings.hasNext());
    }

    public CursorPageResponse<FollowResponse> findFollowers(FollowersCursorPageRequest followersCursorPageRequest) {

        CursorPageResponse<Follow> followings = followRepository.getFollowers(
                followersCursorPageRequest.followeeId(),
                followersCursorPageRequest.pageCursor(),
                followersCursorPageRequest.fetchSize()
        );

        List<FollowResponse> responseContent = followings.content().stream()
                .map(FollowResponse::from)
                .toList();

        return new CursorPageResponse<>(
                responseContent, followings.nextCursor(), followings.hasNext());
    }
}
