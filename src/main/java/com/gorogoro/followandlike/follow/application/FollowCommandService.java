package com.gorogoro.followandlike.follow.application;

import com.gorogoro.followandlike.follow.domain.exception.FollowException;
import com.gorogoro.followandlike.follow.domain.model.Follow;
import com.gorogoro.followandlike.follow.domain.repository.FollowRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.gorogoro.followandlike.follow.domain.exception.FollowErrorCode.UNFOLLOW_PERMISSION_DENIED;

@Service
public class FollowCommandService {

    private final FollowRepository followRepository;

    public FollowCommandService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public void follow(Long followerId, Long followeeId) {
        Follow follow = new Follow(followerId, followeeId);
        try {
            followRepository.save(follow);
        } catch (DataIntegrityViolationException e) {
            // 다른 트랜잭션에서 이미 팔로우 처리가 완료된 경우에도 문제 없이 넘어가야 함. (팔로우 멱등성 보장)
        }
    }

    public void unfollow(Long followerId, Long followeeId) {
        try {
            followRepository.deleteByFollowerIdAndFolloweeId(followerId, followeeId);
        } catch (DataIntegrityViolationException e) {
            // 다른 트랜잭션에서 이미 팔로우 취소가 완료된 경우에도 문제 없이 넘어가야 함. (팔로우 취소 멱등성 보장)
        }
    }

    public void unfollowById(Long userId, Long followId) {
        Optional<Follow> optionalFollow = followRepository.findById(followId);
        if (optionalFollow.isEmpty()) {
            // 이미 취소된 팔로우에 대해서도 문제 없이 넘어가야 함. (팔로우 취소 멱등성 보장)
            return;
        }

        Follow followFound = optionalFollow.get();
        if (!followFound.getFollowerId().equals(userId)) {
            throw new FollowException(UNFOLLOW_PERMISSION_DENIED);
        }

        try {
            followRepository.delete(followFound);
        } catch (DataIntegrityViolationException e) {
            // 다른 트랜잭션에서 이미 팔로우 취소가 완료된 경우에도 문제 없이 넘어가야 함. (팔로우 취소 멱등성 보장)
        }
    }
}
