package com.gorogoro.followandlike.follow.application;

import com.gorogoro.followandlike.follow.application.dto.WhoFollowsWhom;
import com.gorogoro.followandlike.follow.domain.model.Follow;
import com.gorogoro.followandlike.follow.domain.repository.FollowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FollowCommandService {

    private final FollowRepository followRepository;

    public FollowCommandService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    public void follow(WhoFollowsWhom whoFollowsWhom) {
        Follow follow = new Follow(whoFollowsWhom.followerId(), whoFollowsWhom.followeeId());
        followRepository.saveIdempotently(follow);
    }

    public void unfollow(WhoFollowsWhom whoFollowsWhom) {
        // DB에 삭제 대상이 없어도 별도 예외가 발생하지 않으므로 멱등성이 보장된다.
        followRepository.deleteByFollowerIdAndFolloweeId(whoFollowsWhom.followerId(), whoFollowsWhom.followeeId());
    }
}
