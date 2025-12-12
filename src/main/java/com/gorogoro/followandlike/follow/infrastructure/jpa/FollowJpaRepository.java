package com.gorogoro.followandlike.follow.infrastructure.jpa;

import com.gorogoro.followandlike.follow.domain.model.Follow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface FollowJpaRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    Slice<Follow> findAllByFollowerIdOrderByIdDesc(Long followerId, Pageable pageable);

    Slice<Follow> findAllByFollowerIdAndIdLessThanOrderByIdDesc(Long followerId, Long cursor, Pageable pageable);

    Slice<Follow> findAllByFolloweeIdOrderByIdDesc(Long followeeId, Pageable pageable);

    Slice<Follow> findAllByFolloweeIdAndIdLessThanOrderByIdDesc(Long followeeId, Long cursor, Pageable pageable);

    long countByFollowerId(Long followerId);

    long countByFolloweeId(Long followeeId);

    void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId);

    /**
     * MySQL(H2 for test) 의 upsert 문법을 활용한 멱등 insert 수행
     * 예외가 발생하지 않으면 정상 동작한 것으로 간주합니다.
     */
    @Modifying(clearAutomatically = true)
    @Query(
            nativeQuery = true,
            value = "INSERT INTO follow (follower_id, followee_id, created_at)" +
                    "VALUES (:followerId, :followeeId, :now)" +
                    "ON DUPLICATE KEY UPDATE id = id"  // 키 중복 시 사실 상 no-op
    )
    void saveIdempotently(
            @Param("followerId") Long followerId,
            @Param("followeeId") Long followeeId,
            @Param("now") Instant now);
}
