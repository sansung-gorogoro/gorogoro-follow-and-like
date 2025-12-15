package com.gorogoro.followandlike.follow.application;

import com.gorogoro.followandlike.follow.application.dto.FollowResponse;
import com.gorogoro.followandlike.follow.domain.exception.FollowException;
import com.gorogoro.followandlike.follow.application.dto.CursorBasedPaginatedResponse;
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

    public FollowResponse findById(Long id) {
        return FollowResponse.from(
                followRepository.findById(id)
                        .orElseThrow(() -> new FollowException(FOLLOW_NOT_FOUND))
        );
    }

    public FollowResponse findByFollowerIdAndFolloweeId(Long followerId, Long followeeId) {

        Optional<Follow> optionalFollow = followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
        return optionalFollow.map(FollowResponse::from).orElse(null);
    }

    public long countByFollowerId(Long followerId) {
        return followRepository.countByFollowerId(followerId);
    }

    public long countByFolloweeId(Long followeeId) {
        return followRepository.countByFolloweeId(followeeId);
    }

    public CursorBasedPaginatedResult<FollowResponse> findFollowings(Long followerId, Long pageCursor, int fetchSize) {
        CursorBasedPaginatedResult<Follow> followings
    public CursorBasedPaginatedResponse<FollowResponse> findFollowings(Long followerId, Long pageCursor, int fetchSize) {
        CursorBasedPaginatedResponse<Follow> followings
                = followRepository.getFollowings(followerId, pageCursor, fetchSize);

        List<FollowResponse> responseContent = followings.content().stream()
                .map(FollowResponse::from)
                .toList();

        return new CursorBasedPaginatedResponse<>(
                responseContent, followings.nextCursor(), followings.hasNext());
    }

    public CursorBasedPaginatedResult<FollowResponse> findFollowers(Long followeeId, Long pageCursor, int fetchSize) {
        CursorBasedPaginatedResult<Follow> followings
    public CursorBasedPaginatedResponse<FollowResponse> findFollowers(Long followeeId, Long pageCursor, int fetchSize) {
        CursorBasedPaginatedResponse<Follow> followings
                = followRepository.getFollowers(followeeId, pageCursor, fetchSize);

        List<FollowResponse> responseContent = followings.content().stream()
                .map(FollowResponse::from)
                .toList();

        return new CursorBasedPaginatedResponse<>(
                responseContent, followings.nextCursor(), followings.hasNext());
    }
}
