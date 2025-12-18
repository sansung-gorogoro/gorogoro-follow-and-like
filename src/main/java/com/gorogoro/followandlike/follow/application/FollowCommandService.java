package com.gorogoro.followandlike.follow.application;

import com.gorogoro.followandlike.follow.application.client.UserClient;
import com.gorogoro.followandlike.follow.application.dto.WhoFollowsWhom;
import com.gorogoro.followandlike.follow.domain.exception.FollowErrorCode;
import com.gorogoro.followandlike.follow.domain.exception.FollowException;
import com.gorogoro.followandlike.follow.domain.model.Follow;
import com.gorogoro.followandlike.follow.domain.repository.FollowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FollowCommandService {

    private final UserClient userClient;
    private final FollowRepository followRepository;

    public FollowCommandService(FollowRepository followRepository, UserClient userClient) {
        this.userClient = userClient;
        this.followRepository = followRepository;
    }

    public void follow(WhoFollowsWhom whoFollowsWhom) {
        Follow follow = new Follow(whoFollowsWhom.followerId(), whoFollowsWhom.followeeId());

        validateUserExistence(whoFollowsWhom);

        followRepository.saveIdempotently(follow);
    }

    public void unfollow(WhoFollowsWhom whoFollowsWhom) {
        // 언팔로우 시에는 따로 사용자 존재 여부 검증 안 해도 됨
        // DB에 삭제 대상이 없어도 별도 예외가 발생하지 않으므로 멱등성이 보장된다.
        followRepository.deleteByFollowerIdAndFolloweeId(whoFollowsWhom.followerId(), whoFollowsWhom.followeeId());
    }

    // Helpers --------------------

    private void validateUserExistence(WhoFollowsWhom whoFollowsWhom) {
        if (!userClient.exists(whoFollowsWhom.followerId())) {
            throw new FollowException(FollowErrorCode.FOLLOWER_NOT_FOUNT);
        }

        if (!userClient.exists(whoFollowsWhom.followeeId())) {
            throw new FollowException(FollowErrorCode.FOLLOWEE_NOT_FOUNT);
        }
    }
}
