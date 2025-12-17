package com.gorogoro.followandlike.follow.application;

import com.gorogoro.followandlike.follow.application.dto.FollowersCursorPageQuery;
import com.gorogoro.followandlike.follow.application.dto.FollowingsCursorPageQuery;
import com.gorogoro.followandlike.common.application.dto.CursorPageResult;
import com.gorogoro.followandlike.follow.application.dto.FollowQueryResult;
import com.gorogoro.followandlike.follow.application.dto.RequiredNonNegativeId;
import com.gorogoro.followandlike.follow.application.dto.WhoFollowsWhom;
import com.gorogoro.followandlike.follow.domain.exception.FollowErrorCode;
import com.gorogoro.followandlike.follow.domain.exception.FollowException;
import com.gorogoro.followandlike.follow.domain.repository.FollowRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test-clean-console")
class FollowQueryServiceTest {

    private final FollowRepository followRepository;
    private final FollowCommandService followCommandService;
    private final FollowQueryService followQueryService;

    @Autowired
    public FollowQueryServiceTest(
            FollowRepository followRepository,
            FollowCommandService followCommandService,
            FollowQueryService followQueryService
    ) {
        this.followRepository = followRepository;
        this.followCommandService = followCommandService;
        this.followQueryService = followQueryService;
    }

    // Setup & Teardown --------------------

    /*
     * 다음 조건을 만족하는 100 개의 followerId followeeId 입력 쌍
     *
     * [조건]
     * 1. "1 m" 꼴의 쌍이 정확히 13 쌍
     * 2. "1 2" 는 반드시 포함
     * 3. "n 1" 꼴의 쌍이 정확히 17 쌍
     * 4. "2 1" 는 반드시 포함
     */
    private static final String RANDOM_FOLLOW_INPUT = """
            1 29
            2 1
            1 17
            2 7
            5 10
            22 1
            2 20
            3 25
            1 20
            1 2
            27 1
            6 22
            7 16
            4 24
            2 18
            2 14
            3 4
            3 12
            5 23
            3 21
            4 22
            25 1
            6 15
            28 1
            4 28
            4 11
            1 14
            6 13
            21 1
            4 16
            6 24
            4 26
            23 1
            3 29
            3 10
            4 20
            5 19
            2 26
            10 1
            1 11
            3 23
            5 25
            1 24
            1 13
            4 7
            2 24
            6 18
            19 1
            1 5
            4 13
            6 26
            2 22
            15 1
            7 8
            6 11
            4 1
            3 15
            7 21
            7 1
            2 30
            7 10
            5 14
            1 6
            2 3
            7 12
            5 17
            12 1
            6 9
            2 9
            5 29
            7 14
            3 27
            2 28
            6 7
            3 17
            16 1
            5 12
            30 1
            2 16
            18 1
            5 6
            4 30
            4 9
            3 6
            1 9
            6 20
            2 5
            5 21
            7 23
            2 11
            4 18
            1 8
            4 5
            7 19
            3 8
            5 27
            1 3
            26 1
            5 8
            3 19
            """;

    @BeforeAll
    static void validateInputs() {

        int countOneToN = 0;
        boolean isThereOneToTwo = false;
        int countNToOne = 0;
        boolean isThereTwoToOne = false;

        String[] inputTokes = RANDOM_FOLLOW_INPUT.split("\n");
        for (String token : inputTokes) {
            String[] split = token.split(" ");
            long followerId = Long.parseLong(split[0]);
            long followeeId = Long.parseLong(split[1]);

            if (followerId == followeeId) {
                throw new RuntimeException("error 1");
            }
            if (followerId == 1) {
                countOneToN++;
                if (followeeId == 2) {
                    isThereOneToTwo = true;
                }
            }
            if (followeeId == 1) {
                countNToOne++;
                if (followerId == 2) {
                    isThereTwoToOne = true;
                }
            }
        }

        if (countOneToN != 13 || countNToOne != 17) {
            throw new RuntimeException("error 2");
        }
        if (!isThereOneToTwo || !isThereTwoToOne) {
            throw new RuntimeException("error 3");
        }

        System.out.println("countOneToN = " + countOneToN);
        System.out.println("isThereOneToTwo = " + isThereOneToTwo);
        System.out.println("countNToOne = " + countNToOne);
        System.out.println("isThereTwoToOne = " + isThereTwoToOne);
    }


    @BeforeEach
    void setup() {

        String[] inputTokes = RANDOM_FOLLOW_INPUT.split("\n");
        for (String token : inputTokes) {
            String[] split = token.split(" ");
            long followerId = Long.parseLong(split[0]);
            long followeeId = Long.parseLong(split[1]);
            followCommandService.follow(new WhoFollowsWhom(followerId, followeeId));
        }
    }

    @AfterEach
    void teardown() {
        String[] inputTokes = RANDOM_FOLLOW_INPUT.split("\n");
        for (String token : inputTokes) {
            String[] split = token.split(" ");
            long followerId = Long.parseLong(split[0]);
            long followeeId = Long.parseLong(split[1]);
            followCommandService.unfollow(new WhoFollowsWhom(followerId, followeeId));
        }
    }

    // Unit Tests --------------------

    // findById Tests --------------------

    @Test
    void findById_shouldFindFollow_whenTheFollowExists() {

        // @BeforeEach 에서 생성되지 않은 값이어야 함
        final Long FOLLOWER_ID = 100L;
        final Long FOLLOWEE_ID = 200L;

        // Arrange
        followCommandService.follow(new WhoFollowsWhom(FOLLOWER_ID, FOLLOWEE_ID));
        long followId = followRepository.findByFollowerIdAndFolloweeId(FOLLOWER_ID, FOLLOWEE_ID).orElseThrow().getId();

        // Act
        FollowQueryResult result = followQueryService.findById(new RequiredNonNegativeId(followId));

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.followerId()).isEqualTo(FOLLOWER_ID);
        assertThat(result.followeeId()).isEqualTo(FOLLOWEE_ID);

        // Cleanup
        followCommandService.unfollow(new WhoFollowsWhom(FOLLOWER_ID, FOLLOWEE_ID));
    }

    @Test
    void findById_shouldThrowFollowException_whenFollowNotExists() {

        final long notFoundFollowId = 999999L;

        // Act & Assert
        try {
            followQueryService.findById(new RequiredNonNegativeId(notFoundFollowId));
        } catch (FollowException e) {
            assertThat(e.getErrorCode()).isEqualTo(FollowErrorCode.FOLLOW_NOT_FOUND);
            return;
        }

        fail();
    }

    @Test
    void findById_shouldThrowNullPointerException_whenFollowIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.findById(new RequiredNonNegativeId(null)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void findById_shouldThrowIllegalArgumentException_whenFollowIdIsZeroOrNegative() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.findById(new RequiredNonNegativeId(0L)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> followQueryService.findById(new RequiredNonNegativeId(-1L)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // findByFollowerIdAndFolloweeId Tests --------------------

    @Test
    void findByFollowerIdAndFolloweeId_shouldFoundFollow_whenTheFollowExists() {
        // Act
        FollowQueryResult result = followQueryService.findByFollowerIdAndFolloweeId(new WhoFollowsWhom(1L, 2L));

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.followerId()).isEqualTo(1L);
        assertThat(result.followeeId()).isEqualTo(2L);
    }

    @Test
    void findByFollowerIdAndFolloweeId_shouldReturnNull_whenTheFollowNotExists() {
        // Act
        FollowQueryResult result = followQueryService.findByFollowerIdAndFolloweeId(new WhoFollowsWhom(100L, 200L));

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void findByFollowerIdAndFolloweeId_shouldReturnNull_whenFollowerIdIsSameAsFolloweeId() {
        // Act
        FollowQueryResult result = followQueryService.findByFollowerIdAndFolloweeId(new WhoFollowsWhom(1L, 1L));

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void findByFollowerIdAndFolloweeId_shouldThrowNullPointerException_whenEitherIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.findByFollowerIdAndFolloweeId(new WhoFollowsWhom(null, 2L)))
                .isInstanceOf(NullPointerException.class);

        assertThatThrownBy(() -> followQueryService.findByFollowerIdAndFolloweeId(new WhoFollowsWhom(1L, null)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void findByFollowerIdAndFolloweeId_shouldThrowIllegalArgumentException_whenEitherIdIsZeroOrNegative() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.findByFollowerIdAndFolloweeId(new WhoFollowsWhom(0L, 2L)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> followQueryService.findByFollowerIdAndFolloweeId(new WhoFollowsWhom(1L, -1L)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // countByFollowerId Tests --------------------

    @Test
    void countByFollowerId_shouldReturn13_whenFollowerIdIs1() {
        // Act
        long result = followQueryService.countByFollowerId(new RequiredNonNegativeId(1L));

        // Assert
        assertThat(result).isEqualTo(13L);
    }

    @Test
    void countByFollowerId_shouldThrowNullPointerException_whenFollowerIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.countByFollowerId(new RequiredNonNegativeId(null)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void countByFollowerId_shouldThrowIllegalArgumentException_whenFollowerIdIsZeroOrNegative() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.countByFollowerId(new RequiredNonNegativeId(0L)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> followQueryService.countByFollowerId(new RequiredNonNegativeId(-1L)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // countByFolloweeId Tests --------------------

    @Test
    void countByFolloweeId_shouldReturn17_whenFolloweeIdIs1() {
        // Act
        long result = followQueryService.countByFolloweeId(new RequiredNonNegativeId(1L));

        // Assert
        assertThat(result).isEqualTo(17L);
    }

    @Test
    void countByFolloweeId_shouldThrowNullPointerException_whenFolloweeIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.countByFolloweeId(new RequiredNonNegativeId(null)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void countByFolloweeId_shouldThrowIllegalArgumentException_whenFolloweeIdIsZeroOrNegative() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.countByFolloweeId(new RequiredNonNegativeId(0L)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> followQueryService.countByFolloweeId(new RequiredNonNegativeId(-1L)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // findFollowings Tests --------------------
    // nextCursor 가 null 일 때까지 주어진 pageSize 와 같은 크기로 계속해서 조회

    @Test
    void findFollowings_shouldFetchAllOnce_whenFollowerIdIs1AndFetchSizeIs14() {
        // Act
        CursorPageResult<FollowQueryResult> result =
                followQueryService.findFollowings(new FollowingsCursorPageQuery(1L, null, 14));

        // Assert
        assertThat(result.content()).hasSize(13);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void findFollowings_shouldFetchAllOnce_whenFollowerIdIs1AndFetchSizeIs13() {
        // Act
        CursorPageResult<FollowQueryResult> result =
                followQueryService.findFollowings(new FollowingsCursorPageQuery(1L, null, 13));

        // Assert
        assertThat(result.content()).hasSize(13);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void findFollowings_shouldFetch13Pages_whenFollowerIdIs1AndFetchSizeIs1() {
        // Act & Assert
        CursorPageResult<FollowQueryResult> result =
                followQueryService.findFollowings(new FollowingsCursorPageQuery(1L, null, 1));

        int totalCount = 0;
        Long cursor = null;

        while (true) {
            assertThat(result.content().size()).isEqualTo(1);
            totalCount += result.content().size();

            if (!result.hasNext()) {
                break;
            }

            cursor = result.nextCursor();
            result = followQueryService.findFollowings(new FollowingsCursorPageQuery(1L, cursor, 1));
        }

        assertThat(totalCount).isEqualTo(13);
    }

    @Test
    void findFollowings_shouldFetchEmptyPage_whenFollowIsNotExists() {
        // Act
        CursorPageResult<FollowQueryResult> result =
                followQueryService.findFollowings(new FollowingsCursorPageQuery(999L, null, 10));

        // Assert
        assertThat(result.content()).isEmpty();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void findFollowings_shouldThrowNullPointerException_whenFollowerIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.findFollowings(new FollowingsCursorPageQuery(null, null, 10)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void findFollowings_shouldThrowIllegalArgumentException_whenPageCursorIsNegative() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.findFollowings(new FollowingsCursorPageQuery(1L, -1L, 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findFollowings_shouldThrowIllegalArgumentException_whenFetchSizeIsLesserThan1_OrGreaterThan100() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.findFollowings(new FollowingsCursorPageQuery(1L, null, 0)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> followQueryService.findFollowings(new FollowingsCursorPageQuery(1L, null, 101)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // getFollowers --------------------
    // nextCursor 가 null 일 때까지 주어진 pageSize 와 같은 크기로 계속해서 조회

    @Test
    void findFollowers_shouldFetchAllOnce_whenFolloweeIdIs1AndFetchSizeIs18() {
        // Act
        CursorPageResult<FollowQueryResult> result =
                followQueryService.findFollowers(new FollowersCursorPageQuery(1L, null, 18));

        // Assert
        assertThat(result.content()).hasSize(17);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void findFollowers_shouldFetchAllOnce_whenFolloweeIdIs1AndFetchSizeIs17() {
        // Act
        CursorPageResult<FollowQueryResult> result =
                followQueryService.findFollowers(new FollowersCursorPageQuery(1L, null, 17));

        // Assert
        assertThat(result.content()).hasSize(17);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void findFollowers_shouldFetch17Pages_whenFolloweeIdIs1AndFetchSizeIs1() {
        // Act & Assert
        CursorPageResult<FollowQueryResult> result =
                followQueryService.findFollowers(new FollowersCursorPageQuery(1L, null, 1));

        int totalCount = 0;
        Long cursor = null;

        while (true) {
            assertThat(result.content().size()).isEqualTo(1);
            totalCount += result.content().size();

            if (!result.hasNext()) {
                break;
            }

            cursor = result.nextCursor();
            result = followQueryService.findFollowers(new FollowersCursorPageQuery(1L, cursor, 1));
        }

        assertThat(totalCount).isEqualTo(17);
    }

    @Test
    void findFollowers_shouldFetchEmptyPage_whenFollowIsNotExists() {
        // Act
        CursorPageResult<FollowQueryResult> result =
                followQueryService.findFollowers(new FollowersCursorPageQuery(999L, null, 10));

        // Assert
        assertThat(result.content()).isEmpty();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.nextCursor()).isNull();
    }

    @Test
    void findFollowers_shouldThrowNullPointerException_whenFolloweeIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.findFollowers(new FollowersCursorPageQuery(null, null, 10)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void findFollowers_shouldThrowIllegalArgumentException_whenPageCursorIsNegative() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.findFollowers(new FollowersCursorPageQuery(1L, -1L, 10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findFollowers_shouldThrowIllegalArgumentException_whenFetchSizeIsLesserThan1_OrGreaterThan100() {
        // Act & Assert
        assertThatThrownBy(() -> followQueryService.findFollowers(new FollowersCursorPageQuery(1L, null, 0)))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> followQueryService.findFollowers(new FollowersCursorPageQuery(1L, null, 101)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}