package com.gorogoro.followandlike.follow.infrastructure.jpa;

import com.gorogoro.followandlike.follow.domain.model.Follow;
import com.gorogoro.followandlike.common.application.dto.CursorPageResult;
import com.gorogoro.followandlike.follow.domain.repository.FollowRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class FollowRepositoryJpaAdapter implements FollowRepository {

    private final FollowJpaRepository followJpaRepository;

    public FollowRepositoryJpaAdapter(FollowJpaRepository followJpaRepository) {
        this.followJpaRepository = followJpaRepository;
    }

    // Query --------------------

    @Override
    public Optional<Follow> findById(Long id) {
        return followJpaRepository.findById(id);
    }

    @Override
    public Optional<Follow> findByFollowerIdAndFolloweeId(Long followerId, Long followeeId) {
        Objects.requireNonNull(followerId, "followerId");
        Objects.requireNonNull(followeeId, "followeeId");
        return followJpaRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Override
    public long countByFollowerId(Long followerId) {
        return followJpaRepository.countByFollowerId(followerId);
    }

    @Override
    public long countByFolloweeId(Long followeeId) {
        return followJpaRepository.countByFolloweeId(followeeId);
    }

    // Paging --------------------

    private final int ZERO_FIXED = 0;
    private final String PAGE_SORT_CRITERIA_FIELD_NAME = "id";

    /**
     * followerId 의 정렬된 팔로"우" 목록 중 pageCursor 이후 fetchSize 만큼 가져옵니다.
     * @param followerId null 안 됨
     * @param pageCursor exclusive, null 인 경우 첫 페이지 반환함
     * @param fetchSize MIN_FETCH_SIZE 보다 커야하고, MAX_FETCH_SIZE 보다 작아야 함.
     */
    @Override
    public CursorPageResult<Follow> getFollowings(Long followerId, Long pageCursor, int fetchSize) {

        Pageable pageable = getPageable(fetchSize);

        Slice<Follow> slice;
        if (pageCursor == null) {  // 첫 페이지
            slice = followJpaRepository.findAllByFollowerIdOrderByIdDesc(followerId, pageable);
        } else {  // 첫 페이지 이후
            slice = followJpaRepository.findAllByFollowerIdAndIdLessThanOrderByIdDesc(followerId, pageCursor, pageable);
        }

        List<Follow> content = sliceToFollows(slice);
        Long nextCursor = getNextCursor(content, slice);
        return new CursorPageResult<>(content, nextCursor, slice.hasNext());
    }

    /**
     * followeeId 의 정렬된 팔로"잉" 목록 중 pageCursor 이후 fetchSize 만큼 가져옵니다.
     * @param followeeId null 안 됨
     * @param pageCursor exclusive, null 인 경우 첫 페이지 반환함
     * @param fetchSize MIN_FETCH_SIZE 보다 커야하고, MAX_FETCH_SIZE 보다 작아야 함.
     */
    @Override
    public CursorPageResult<Follow> getFollowers(Long followeeId, Long pageCursor, int fetchSize) {

        Pageable pageable = getPageable(fetchSize);

        Slice<Follow> slice;
        if (pageCursor == null) {  // 첫 페이지
            slice = followJpaRepository.findAllByFolloweeIdOrderByIdDesc(followeeId, pageable);
        } else {  // 첫 페이지 이후
            slice = followJpaRepository.findAllByFolloweeIdAndIdLessThanOrderByIdDesc(followeeId, pageCursor, pageable);
        }

        List<Follow> content = sliceToFollows(slice);
        Long nextCursor = getNextCursor(content, slice);
        return new CursorPageResult<>(content, nextCursor, slice.hasNext());
    }

    // Command --------------------

    @Override
    public Follow save(Follow follow) {
        return followJpaRepository.save(follow);
    }

    @Override
    public void saveIdempotently(Follow follow) {
        // 네이티브 쿼리 사용 시 JPA Auditing 기능 적용되지 않으므로 직접 createdAt 을 넣어줘야됨
        followJpaRepository.saveIdempotently(follow.getFollowerId(), follow.getFolloweeId(), Instant.now());
    }

    @Override
    public void delete(Follow follow) {
        followJpaRepository.delete(follow);
    }

    @Override
    public void deleteByFollowerIdAndFolloweeId(Long followerId, Long followeeId) {
        followJpaRepository.deleteByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    // Helper methods --------------------

    private Pageable getPageable(int fetchSize) {
        return PageRequest.of(
                ZERO_FIXED,
                fetchSize,
                Sort.by(
                        Sort.Direction.DESC,
                        PAGE_SORT_CRITERIA_FIELD_NAME
                )
        );
    }

    private static List<Follow> sliceToFollows(Slice<Follow> slice) {
        return slice.getContent();
    }

    private static @Nullable Long getNextCursor(List<Follow> content, Slice<Follow> slice) {
        Long nextCursor = null;
        if (!content.isEmpty() && slice.hasNext()) {
            nextCursor = content.getLast().getId();
        }
        return nextCursor;
    }
}
