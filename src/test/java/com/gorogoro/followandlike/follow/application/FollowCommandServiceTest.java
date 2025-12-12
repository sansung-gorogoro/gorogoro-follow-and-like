package com.gorogoro.followandlike.follow.application;

import com.gorogoro.followandlike.follow.domain.model.Follow;
import com.gorogoro.followandlike.follow.domain.repository.FollowRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test-clean-console")
@Transactional
class FollowCommandServiceTest {

    private final FollowRepository followRepository;
    private final FollowCommandService followCommandService;

    @Autowired
    public FollowCommandServiceTest(
            FollowRepository followRepository,
            FollowCommandService followCommandService
    ) {
        this.followRepository = followRepository;
        this.followCommandService = followCommandService;
    }

    // Setup & Teardown --------------------

    private final Long FOLLOWER_ID = 1L;
    private final Long FOLLOWEE_ID = 2L;

    @BeforeEach
    void setup() {
        followCommandService.follow(FOLLOWER_ID, FOLLOWEE_ID);
    }

    @AfterEach
    void teardown() {
        followCommandService.unfollow(FOLLOWER_ID, FOLLOWEE_ID);
    }

    // Unit Tests --------------------

    @Test
    void shouldFollowIdempotent() {

        Follow found1 = followRepository.findByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)
                .get();  // 문제 발생 하면 실패 의도함
        long followeeCount1 = followRepository.countByFollowerId(FOLLOWER_ID);
        long followerCount1 = followRepository.countByFolloweeId(FOLLOWEE_ID);

        followCommandService.follow(FOLLOWER_ID, FOLLOWEE_ID);
        followCommandService.follow(FOLLOWER_ID, FOLLOWEE_ID);
        followCommandService.follow(FOLLOWER_ID, FOLLOWEE_ID);

        Follow found2 = followRepository.findByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)
                .get();  // 문제 발생 하면 실패 의도함
        long followeeCount2 = followRepository.countByFollowerId(FOLLOWER_ID);
        long followerCount2 = followRepository.countByFolloweeId(FOLLOWEE_ID);

        assertAll(
                () -> assertNotNull(found1),
                () -> assertNotEquals(0, followerCount1),
                () -> assertNotEquals(0, followeeCount1),
                () -> assertNotNull(found2),
                () -> assertNotEquals(0, followerCount2),
                () -> assertNotEquals(0, followeeCount2),
                () -> assertEquals(found1, found2),
                () -> assertEquals(followerCount1, followerCount2),
                () -> assertEquals(followeeCount1, followeeCount2)
        );
    }

    @Test
    void shouldUnfollowIdempotent() {
        Follow found = followRepository.findByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID)
                .get();  // 문제 발생 하면 실패 의도함
        long followeeCount = followRepository.countByFollowerId(FOLLOWER_ID);
        long followerCount = followRepository.countByFolloweeId(FOLLOWEE_ID);

        followCommandService.unfollow(FOLLOWER_ID, FOLLOWEE_ID);
        followCommandService.unfollow(FOLLOWER_ID, FOLLOWEE_ID);
        followCommandService.unfollow(FOLLOWER_ID, FOLLOWEE_ID);

        assertAll(
                () -> assertNotNull(found),
                () -> assertEquals(1, followerCount),
                () -> assertEquals(1, followeeCount),
                () -> assertTrue(followRepository.findById(found.getId()).isEmpty()),
                () -> assertEquals(followeeCount - 1, followRepository.countByFolloweeId(FOLLOWEE_ID)),
                () -> assertEquals(followerCount - 1, followRepository.countByFollowerId(FOLLOWER_ID))
        );
    }
}
