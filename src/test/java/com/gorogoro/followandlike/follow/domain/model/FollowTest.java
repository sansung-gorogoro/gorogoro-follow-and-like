package com.gorogoro.followandlike.follow.domain.model;

import com.gorogoro.followandlike.follow.domain.exception.FollowException;
import org.junit.jupiter.api.Test;

import static com.gorogoro.followandlike.follow.domain.exception.FollowErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

class FollowTest {

    @Test
    void shouldSuccess_whenCreation_givenIdsIsValidlyDifferent() {
        Long followerId = 1L;
        Long followeeId = 2L;
        Follow follow = new Follow(followerId, followeeId);
        assertAll(
                () -> assertEquals(1L, follow.getFollowerId()),
                () -> assertEquals(2L, follow.getFolloweeId())
        );
    }

    @Test
    void shouldThrowFollowException_whenCreation_givenFollowerIdIsSameWithFolloweeId() {
        assertThrows(FollowException.class, () -> {

            Long followerId = 1L;
            Long followeeId = followerId;

            try {
                new Follow(followerId, followeeId);
            } catch (FollowException e) {
                assertEquals(FOLLOWED_ONESELF, e.getErrorCode());
                throw e;
            }
        });
    }

    @Test
    void shouldThrowFollowException_whenCreation_givenFollowerIdIsNull() {
        assertThrows(FollowException.class, () -> {

            Long followerId = null;
            Long followeeId = 1L;

            try {
                new Follow(followerId, followeeId);
            } catch (FollowException e) {
                assertEquals(FOLLOWER_ID_IS_NULL, e.getErrorCode());
                throw e;
            }
        });
    }

    @Test
    void shouldThrowFollowException_whenCreation_givenFollowerIdIsNegative() {
        assertThrows(FollowException.class, () -> {

            Long followerId = -1L;
            Long followeeId = 1L;

            try {
                new Follow(followerId, followeeId);
            } catch (FollowException e) {
                assertEquals(FOLLOWER_ID_IS_NEGATIVE, e.getErrorCode());
                throw e;
            }
        });
    }

    @Test
    void shouldThrowFollowException_whenCreation_givenFolloweeIdIsNull() {
        assertThrows(FollowException.class, () -> {

            Long followerId = 1L;
            Long followeeId = null;

            try {
                new Follow(followerId, followeeId);
            } catch (FollowException e) {
                assertEquals(FOLLOWEE_ID_IS_NULL, e.getErrorCode());
                throw e;
            }
        });
    }

    @Test
    void shouldThrowFollowException_whenCreation_givenFolloweeIdIsNegative() {
        assertThrows(FollowException.class, () -> {

            Long followerId = 1L;
            Long followeeId = -1L;

            try {
                new Follow(followerId, followeeId);
            } catch (FollowException e) {
                assertEquals(FOLLOWEE_ID_IS_NEGATIVE, e.getErrorCode());
                throw e;
            }
        });
    }
}