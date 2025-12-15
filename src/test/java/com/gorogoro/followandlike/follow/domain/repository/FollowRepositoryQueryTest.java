package com.gorogoro.followandlike.follow.domain.repository;

import com.gorogoro.followandlike.follow.domain.model.Follow;
import com.gorogoro.followandlike.follow.application.dto.CursorBasedPaginatedResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
@ActiveProfiles("test-clean-console")
class FollowRepositoryQueryTest {

    private final FollowRepository followRepository;

    @Autowired
    public FollowRepositoryQueryTest(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    // Setup & Teardown --------------------

    private final long CRITERIA_MEMBER_ID_ONE = 1L;
    private final int NUM_MEMBERS = 11;  // == last member id
    int NUM_TOTAL_FOLLOWS = (NUM_MEMBERS - 1) * 2;
    long autoIncrementIdOffset = 0;

    @BeforeEach
    void setUp() {
        for (long otherMemberId = 2L; otherMemberId <= NUM_MEMBERS; otherMemberId++) {
            // CRITERIA_MEMBER <-> 다른 회원 상호 팔로우(맞팔)
            if (otherMemberId == 2L) {
                Follow first = followRepository.save(new Follow(CRITERIA_MEMBER_ID_ONE, otherMemberId));
                autoIncrementIdOffset = first.getId() - 1;
            } else {
                followRepository.save(new Follow(CRITERIA_MEMBER_ID_ONE, otherMemberId));
            }
            followRepository.save(new Follow(otherMemberId, CRITERIA_MEMBER_ID_ONE));
        }
    }

    // Unit Tests --------------------

    @Test
    void shouldFind_whenExists() {
        Follow found = followRepository.findByFollowerIdAndFolloweeId(1L, 2L)
                .orElseThrow(() -> new RuntimeException("not found"));

        println(toJson(found));
    }

    @Test
    void countByFollowerId() {
        assertEquals(NUM_MEMBERS - 1, followRepository.countByFollowerId(CRITERIA_MEMBER_ID_ONE));
        for (var followeeId = CRITERIA_MEMBER_ID_ONE + 1; followeeId <= NUM_MEMBERS; followeeId++) {
            assertEquals(1, followRepository.countByFollowerId(followeeId));
        }
    }

    @Test
    void countByFolloweeId() {
        assertEquals(NUM_MEMBERS - 1, followRepository.countByFolloweeId(CRITERIA_MEMBER_ID_ONE));
        for (var followeeId = CRITERIA_MEMBER_ID_ONE + 1; followeeId <= NUM_MEMBERS; followeeId++) {
            assertEquals(1, followRepository.countByFolloweeId(followeeId));
        }
    }

    @Test
    void getFollowings() {
        /*
         * 1 은 2, ..., 11 과 맞팔로우하고 있으며 내림차순 정렬되어 페이징 됨
         * - ex. id=1: 1L -> 2L, id=2: 2L -> 1L / id=3: 1L -> 3L, id=4: 3L -> 1L / ...
         *
         * 따라서, 페이지 크기가 3일 때, 1이 팔로우하는 목록(followings)는 다음과 같이 조회되어야 함
         * 19, 17, 15 (nextCursor = 15, hasNext = true)
         * 13, 11, 9  (nextCursor = 9, hasNext = true)
         * 7 , 5 , 3  (nextCursor = 3, hasNext = true)
         * 1          (nextCursor = null, hasNext = false)
         */
        int NUM_FOLLOWINGS_OF_CRITERIA_MEMBER = NUM_MEMBERS - 1;
        int FETCH_SIZE = 3;
        int NUM_ROWS = NUM_FOLLOWINGS_OF_CRITERIA_MEMBER
                + (NUM_FOLLOWINGS_OF_CRITERIA_MEMBER / FETCH_SIZE)
                + (NUM_FOLLOWINGS_OF_CRITERIA_MEMBER % FETCH_SIZE == 0 ? 0 : 1);
        int NUM_COLS = 3;

        // page: {followerId, followeeId, id} x FETCH_SIZE, {nextCursor, hasNext(1 or 0), null}
        Long[][] expected = new Long[NUM_ROWS][NUM_COLS];
        long curFolloweeId = NUM_MEMBERS + 1;
        long curFollowId = NUM_TOTAL_FOLLOWS + 1 + autoIncrementIdOffset;  // 홀수
        for (int r = 0; r < NUM_ROWS - 1; r++) {

            if (r % (FETCH_SIZE + 1) == FETCH_SIZE) {
                expected[r][0] = curFollowId;
                expected[r][1] = 1L;  // true
                expected[r][2] = null;
            } else {
                curFolloweeId -= 1;
                curFollowId -= 2;

                expected[r][0] = CRITERIA_MEMBER_ID_ONE;
                expected[r][1] = curFolloweeId;
                expected[r][2] = curFollowId;
            }
        }
        expected[NUM_ROWS - 1][0] = null;
        expected[NUM_ROWS - 1][1] = 0L;
        expected[NUM_ROWS - 1][2] = null;

        Long[][] actual = new Long[NUM_ROWS][NUM_COLS];
        CursorBasedPaginatedResponse<Follow> pageFound = null;
        int r = 0;
        do {
            Long nextCursor = (pageFound == null) ? null : pageFound.nextCursor();
            pageFound = followRepository.getFollowings(CRITERIA_MEMBER_ID_ONE, nextCursor, FETCH_SIZE);
            List<Follow> content = pageFound.content();

            for (Follow follow : content) {
                actual[r][0] = follow.getFollowerId();
                actual[r][1] = follow.getFolloweeId();
                actual[r][2] = follow.getId();
                r++;
            }

            actual[r][0] = pageFound.nextCursor();
            actual[r][1] = pageFound.hasNext() ? 1L : 0L;
            actual[r][2] = null;

            r++;

            println(toJson(pageFound));
        } while (pageFound.hasNext());

        System.out.println("===== expected =====");
        println(toJson(expected));
        System.out.println("===== actual =====");
        println(toJson(actual));

        assertTrue(Objects.deepEquals(expected, actual));
    }

    @Test
    void getFollowers() {
        /*
         * 1 은 2, ..., 11 과 맞팔로우하고 있으며 내림차순 정렬되어 페이징 됨
         * - ex. id=1: 1L -> 2L, id=2: 2L -> 1L / id=3: 1L -> 3L, id=4: 3L -> 1L / ...
         *
         * 따라서, 페이지 크기가 3일 때, 1을 팔로우하는 목록(followers)는 다음과 같이 조회되어야 함
         * 20, 18, 16 (nextCursor = 16, hasNext = true)
         * 14, 12, 10 (nextCursor = 10, hasNext = true)
         * 8 , 6 , 4  (nextCursor = 4, hasNext = true)
         * 2          (nextCursor = null, hasNext = false)
         */
        int NUM_FOLLOWINGS_OF_CRITERIA_MEMBER = NUM_MEMBERS - 1;
        int FETCH_SIZE = 3;
        int NUM_ROWS = NUM_FOLLOWINGS_OF_CRITERIA_MEMBER
                + (NUM_FOLLOWINGS_OF_CRITERIA_MEMBER / FETCH_SIZE)
                + (NUM_FOLLOWINGS_OF_CRITERIA_MEMBER % FETCH_SIZE == 0 ? 0 : 1);
        int NUM_COLS = 3;

        // page: {followerId, followeeId, id} x FETCH_SIZE, {nextCursor, hasNext(1 or 0), null}
        Long[][] expected = new Long[NUM_ROWS][NUM_COLS];
        long curFollowerId = NUM_MEMBERS + 1;
        long curFollowId = NUM_TOTAL_FOLLOWS + 2 + autoIncrementIdOffset;  // 짝수
        for (int r = 0; r < NUM_ROWS - 1; r++) {

            if (r % (FETCH_SIZE + 1) == FETCH_SIZE) {
                expected[r][0] = curFollowId;
                expected[r][1] = 1L;  // true
                expected[r][2] = null;
            } else {
                curFollowerId -= 1;
                curFollowId -= 2;

                expected[r][0] = curFollowerId;
                expected[r][1] = CRITERIA_MEMBER_ID_ONE;
                expected[r][2] = curFollowId;
            }
        }
        expected[NUM_ROWS - 1][0] = null;
        expected[NUM_ROWS - 1][1] = 0L;
        expected[NUM_ROWS - 1][2] = null;

        Long[][] actual = new Long[NUM_ROWS][NUM_COLS];
        CursorBasedPaginatedResponse<Follow> pageFound = null;
        int r = 0;
        do {
            Long nextCursor = (pageFound == null) ? null : pageFound.nextCursor();
            pageFound = followRepository.getFollowers(CRITERIA_MEMBER_ID_ONE, nextCursor, FETCH_SIZE);
            List<Follow> content = pageFound.content();

            for (Follow follow : content) {
                actual[r][0] = follow.getFollowerId();
                actual[r][1] = follow.getFolloweeId();
                actual[r][2] = follow.getId();
                r++;
            }

            actual[r][0] = pageFound.nextCursor();
            actual[r][1] = pageFound.hasNext() ? 1L : 0L;
            actual[r][2] = null;

            r++;

            println(toJson(pageFound));
        } while (pageFound.hasNext());

        System.out.println("===== expected =====");
        println(toJson(expected));
        System.out.println("===== actual =====");
        println(toJson(actual));

        assertTrue(Objects.deepEquals(expected, actual));
    }

    // Helper Methods --------------------

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void printStartSeparatorLine() {
        System.out.println("\n***********************\n");
    }

    void println(String line) {
        System.out.println(line);
    }

    @AfterEach
    void printEndSeparatorLine() {
        System.out.println("\n***********************\n");
    }

    String toJson(Object o) {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    }
}