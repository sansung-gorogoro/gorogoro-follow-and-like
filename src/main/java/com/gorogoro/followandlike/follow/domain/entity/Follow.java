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

            validateId(followerId, followeeId);

            this.followerId = followerId;
            this.followeeId = followeeId;
        }

        private static void validateId(Long followerId, Long followeeId) {
            if (followerId == null || followeeId == null) {
                throw new FollowException("followerId 또는 followeeId 가 null 입니다.");
            }

            if (followerId < 0 || followeeId < 0) {
                throw new FollowException("followerId 또는 followeeId 가 음수입니다.");
            }

            if (followerId.equals(followeeId)) {
                throw new FollowException("자기자신을 팔로우할 수 없습니다. (followerId 와 followeeId 가 같습니다.)");
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
