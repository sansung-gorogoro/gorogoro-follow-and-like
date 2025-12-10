package com.gorogoro.followandlike.follow.infrastructure.jpa;

import com.gorogoro.followandlike.follow.domain.model.Follow;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
