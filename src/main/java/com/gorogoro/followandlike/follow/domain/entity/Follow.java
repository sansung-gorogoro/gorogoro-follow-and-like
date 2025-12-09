package com.gorogoro.followandlike.follow.domain.entity;

import com.gorogoro.followandlike.common.domain.entity.CreationAuditEntity;
import com.gorogoro.followandlike.follow.domain.exception.FollowException;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.gorogoro.followandlike.follow.domain.exception.FollowErrorCode.*;

@Entity
@Table(
    name = "follow",
    uniqueConstraints = @UniqueConstraint(
            name = "uq_follower_id_followee_id",
            columnNames = {"follower_id", "followee_id"}
    ),
    check = @CheckConstraint(
            name = "follower_must_different_from_followee",
            constraint = "follower_id <> followee_id"
    ),
    indexes = @Index(
            name = "idx_followee_id",
            columnList = "followee_id"
    )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends CreationAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Getter
    private Long followerId;

    @Column(nullable = false)
    @Getter
    private Long followeeId;

    public Follow(Long followerId, Long followeeId) {

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
        if (!(o instanceof Follow follow)) return false;
        return Objects.equals(id, follow.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
