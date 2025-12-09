package com.gorogoro.followandlike.follow.domain.entity;

import com.gorogoro.followandlike.follow.domain.entity.Follow.WhoFollowsWhom;
import com.gorogoro.followandlike.follow.domain.exception.FollowException;
import org.junit.jupiter.api.Test;

import static com.gorogoro.followandlike.follow.domain.exception.FollowErrorCode.*;
import static org.junit.jupiter.api.Assertions.*;

class FollowTest {

    @Test
    void shouldSuccess_whenEntityCreation_givenIdsIsValidlyDifferent() {
        WhoFollowsWhom id = new WhoFollowsWhom(1L, 2L);
        Follow follow = new Follow(id);
        assertAll(
                () -> assertEquals(1L, follow.getId().getFollowerId()),
                () -> assertEquals(2L, follow.getId().getFolloweeId())
        );
    }

    @Test
    void shouldThrownFollowException_whenEntityCreation_givenIdIsNull() {
        assertThrows(FollowException.class, () -> {
            try {
                new Follow(null);
            } catch (FollowException e) {
                assertEquals(ID_IS_NULL, e.getCode());
                throw e;
            }
        });
    }

    @Test
    void shouldThrownFollowException_whenEntityCreation_givenFollowerIdIsSameWithFolloweeId() {
        assertThrows(FollowException.class, () -> {
            try {
                new Follow(new WhoFollowsWhom(1L, 1L));
            } catch (FollowException e) {
                assertEquals(FOLLOWED_ONESELF, e.getCode());
                throw e;
            }
        });
    }

    @Test
    void shouldThrownFollowException_whenIdCreation_givenFollowerIdIsNull() {
        assertThrows(FollowException.class, () -> {
            try {
                new WhoFollowsWhom(null, 1L);
            } catch (FollowException e) {
                assertEquals(FOLLOWER_ID_IS_NULL, e.getCode());
                throw e;
            }
        });
    }

    @Test
    void shouldThrownFollowException_whenIdCreation_givenFollowerIdIsNegative() {
        assertThrows(FollowException.class, () -> {
            try {
                new WhoFollowsWhom(-2L, 1L);
            } catch (FollowException e) {
                assertEquals(FOLLOWER_ID_IS_NEGATIVE, e.getCode());
                throw e;
            }
        });
    }

    @Test
    void shouldThrownFollowException_whenIdCreation_givenFolloweeIdIsNull() {
        assertThrows(FollowException.class, () -> {
            try {
                new WhoFollowsWhom(1L, null);
            } catch (FollowException e) {
                assertEquals(FOLLOWEE_ID_IS_NULL, e.getCode());
                throw e;
            }
        });
    }

    @Test
    void shouldThrownFollowException_whenIdCreation_givenFolloweeIdIsNegative() {
        assertThrows(FollowException.class, () -> {
            try {
                new WhoFollowsWhom(1L, -2L);
            } catch (FollowException e) {
                assertEquals(FOLLOWEE_ID_IS_NEGATIVE, e.getCode());
                throw e;
            }
        });
    }

    @Test
    void shouldTwoEntitiesAreIdentical_whenCompareTwoEntity_givenEntitiesWhichHaveSameId() {
        Follow one = new Follow(new WhoFollowsWhom(1L, 2L));
        Follow another = new Follow(new WhoFollowsWhom(1L, 2L));

        assertEquals(one, another);
    }

    @Test
    void shouldTwoEntitiesAreDifferent_whenCompareTwoEntity_givenEntitiesWhichHaveDifferentId() {
        Follow one = new Follow(new WhoFollowsWhom(1L, 2L));
        Follow another = new Follow(new WhoFollowsWhom(2L, 1L));

        assertNotEquals(one, another);
    }
}