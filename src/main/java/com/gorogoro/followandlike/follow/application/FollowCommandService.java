package com.gorogoro.followandlike.follow.application;

import com.gorogoro.followandlike.follow.domain.exception.FollowException;
import com.gorogoro.followandlike.follow.domain.model.Follow;
import com.gorogoro.followandlike.follow.domain.repository.FollowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.gorogoro.followandlike.follow.domain.exception.FollowErrorCode.UNFOLLOW_PERMISSION_DENIED;

@Service
@Transactional
public class FollowCommandService {

    private final FollowRepository followRepository;

    public FollowCommandService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public void follow(Long followerId, Long followeeId) {
        Follow follow = new Follow(followerId, followeeId);
        followRepository.saveIdempotently(follow);
    }

    public void unfollow(Long followerId, Long followeeId) {
        // DB에 삭제 대상이 없어도 별도 예외가 발생하지 않으므로 멱등성이 보장된다.
        followRepository.deleteByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    public void unfollowById(Long userId, Long followId) {
        Optional<Follow> optionalFollow = followRepository.findById(followId);
        if (optionalFollow.isEmpty()) {
            // 언팔로우 멱등성 보장
            return;
        }

        // 권한 확인(내 것만 언팔 가능)
        Follow followFound = optionalFollow.get();
        if (!followFound.getFollowerId().equals(userId)) {
            throw new FollowException(UNFOLLOW_PERMISSION_DENIED);
        }

        // DB에 삭제 대상이 없어도 별도 예외가 발생하지 않으므로 멱등성이 보장된다.
        followRepository.delete(followFound);
    }
}
