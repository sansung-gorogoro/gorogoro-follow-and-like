package com.gorogoro.followandlike.follow.domain.entity;

import com.gorogoro.followandlike.common.domain.entity.CreationAuditEntity;
import com.gorogoro.followandlike.follow.domain.exception.FollowException;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

import static com.gorogoro.followandlike.follow.domain.exception.FollowErrorCode.*;

@Entity
@Table(
    name = "follow",
    check = @CheckConstraint(name = "follower_must_different_from_followee", constraint = "follower_id <> followee_id")
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends CreationAuditEntity {

    @EmbeddedId
    @Getter
    private WhoFollowsWhom id;

    public Follow(WhoFollowsWhom id) {
        if (id == null) {
            throw new FollowException(ID_IS_NULL);
        }
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Follow follow)) return false;
        return Objects.equals(id, follow.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    /* ↓ 내부 키 클래스 ↓ */

    @Embeddable
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class WhoFollowsWhom implements Serializable {

        @Column(nullable = false)
        @Getter
        private Long followerId;

        @Column(nullable = false)
        @Getter
        private Long followeeId;

        public WhoFollowsWhom(Long followerId, Long followeeId) {

            validate(followerId, followeeId);

            this.followerId = followerId;
            this.followeeId = followeeId;
        }

        private void validate(Long followerId, Long followeeId) {
            if (followerId == null) {
                throw new FollowException(FOLLOWER_ID_IS_NULL);
            }
            if (followeeId == null) {
                throw new FollowException(FOLLOWEE_ID_IS_NULL);
            }
            if (followerId < 0) {
                throw new FollowException(FOLLOWER_ID_IS_NEGATIVE);
            }
            if (followeeId < 0) {
                throw new FollowException(FOLLOWEE_ID_IS_NEGATIVE);
            }
            if (followerId.equals(followeeId)) {
                throw new FollowException(FOLLOWED_ONESELF);
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof WhoFollowsWhom that)) return false;
            return Objects.equals(followerId, that.followerId) && Objects.equals(followeeId, that.followeeId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(followerId, followeeId);
        }
    }
}
