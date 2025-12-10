package com.gorogoro.followandlike.follow.infrastructure.jpa;

import com.gorogoro.followandlike.follow.domain.model.Follow;
import com.gorogoro.followandlike.follow.domain.repository.FollowRepository;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

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
    public Optional<Follow> findById(@NonNull Long id) {
        return followJpaRepository.findById(id);
    }

    @Override
    public Optional<Follow> findByFollowerIdAndFolloweeId(@NonNull Long followerId, @NonNull Long followeeId) {
        Objects.requireNonNull(followerId, "followerId");
        Objects.requireNonNull(followeeId, "followeeId");
        return followJpaRepository.findByFollowerIdAndFolloweeId(followerId, followeeId);
    }

    @Override
    public long countByFollowerId(@NonNull Long followerId) {
        return followJpaRepository.countByFollowerId(followerId);
    }

    @Override
    public long countByFolloweeId(@NonNull Long followeeId) {
        return followJpaRepository.countByFolloweeId(followeeId);
    }

    // Paging --------------------

    private final int MIN_FETCH_SIZE = 1;
    private final int MAX_FETCH_SIZE = 100;
    private final int ZERO_FIXED = 0;
    private final String PAGE_SORT_CRITERIA_FIELD_NAME = "id";

    /**
     * followerId 의 정렬된 팔로"우" 목록 중 pageCursor 이후 fetchSize 만큼 가져옵니다.
     * @param followerId null 안 됨
     * @param pageCursor exclusive, null 인 경우 첫 페이지 반환함
     * @param fetchSize MIN_FETCH_SIZE 보다 커야하고, MAX_FETCH_SIZE 보다 작아야 함.
     */
    @Override
    public CursorBasedPaginatedResult<Follow> getFollowings(@NonNull Long followerId, Long pageCursor, int fetchSize) {

        validateFetchSize(fetchSize);

        Pageable pageable = getPageable(fetchSize);

        Slice<Follow> slice;
        if (pageCursor == null) {  // 첫 페이지
            slice = followJpaRepository.findAllByFollowerIdOrderByIdDesc(followerId, pageable);
        } else {  // 첫 페이지 이후
            slice = followJpaRepository.findAllByFollowerIdAndIdLessThanOrderByIdDesc(followerId, pageCursor, pageable);
        }

        List<Follow> content = sliceToFollows(slice);
        Long nextCursor = getNextCursor(content, slice);
        return new CursorBasedPaginatedResult<>(content, nextCursor, slice.hasNext());
    }

    /**
     * followeeId 의 정렬된 팔로"잉" 목록 중 pageCursor 이후 fetchSize 만큼 가져옵니다.
     * @param followeeId null 안 됨
     * @param pageCursor exclusive, null 인 경우 첫 페이지 반환함
     * @param fetchSize MIN_FETCH_SIZE 보다 커야하고, MAX_FETCH_SIZE 보다 작아야 함.
     */
    @Override
    public CursorBasedPaginatedResult<Follow> getFollowers(@NonNull Long followeeId, Long pageCursor, int fetchSize) {

        validateFetchSize(fetchSize);

        Pageable pageable = getPageable(fetchSize);

        Slice<Follow> slice;
        if (pageCursor == null) {  // 첫 페이지
            slice = followJpaRepository.findAllByFolloweeIdOrderByIdDesc(followeeId, pageable);
        } else {  // 첫 페이지 이후
            slice = followJpaRepository.findAllByFolloweeIdAndIdLessThanOrderByIdDesc(followeeId, pageCursor, pageable);
        }

        List<Follow> content = sliceToFollows(slice);
        Long nextCursor = getNextCursor(content, slice);
        return new CursorBasedPaginatedResult<>(content, nextCursor, slice.hasNext());
    }

    // Command --------------------

    @Override
    public Follow save(@NonNull Follow follow) {
        return followJpaRepository.save(follow);
    }

    @Override
    // Helper methods --------------------

    private void validateFetchSize(int fetchSize) {
        if (fetchSize < MIN_FETCH_SIZE || MAX_FETCH_SIZE < fetchSize) {
            throw new IllegalArgumentException(
                    "fetchSize 가 " + MIN_FETCH_SIZE +" 보다 작거나 " + MAX_FETCH_SIZE + " 보다 큽니다.");
        }
    }

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
