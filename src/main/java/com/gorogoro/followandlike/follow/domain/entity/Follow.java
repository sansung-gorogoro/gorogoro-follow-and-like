package com.gorogoro.followandlike.follow.domain.entity;

import com.gorogoro.followandlike.common.domain.entity.CreationAuditEntity;
import com.gorogoro.followandlike.follow.domain.exception.FollowException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/*
 * 비즈니스 규칙
 * 1. 회원은 자기자신을 팔로우 할 수 없다: follower_id != followee_id
 * 2. 회원은 자신의 팔로우만 취소(unfollow)할 수 있다.
 * 3. unfollow == soft delete
 * 4. 팔로우 및 언팔로우는 멱등성을 가져야한다.
 *    4.1. 유일키 제약조건: follower_id, followee_id
 *    4.2. 중복이 존재하는 경우 별도 예외 발생 x
 *    4.3. 삭제 대상이 존재하지 않는 경우 별도 예외 발생 x
 */
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
