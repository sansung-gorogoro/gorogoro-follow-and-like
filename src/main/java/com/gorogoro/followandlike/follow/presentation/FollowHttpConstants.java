package com.gorogoro.followandlike.follow.presentation;

public interface FollowHttpConstants {
    int MIN_PAGE_SIZE = 1;
    int MAX_PAGE_SIZE = 100;

    String PARAM_NAME_FOLLOW_ID = "id";
    String PARAM_NAME_FOLLOWER_ID = "followerId";
    String PARAM_NAME_FOLLOWEE_ID = "followeeId";
    String PARAM_NAME_CREATED_AT = "createdAt";
    String PARAM_NAME_PAGE_CURSOR = "pageCursor";
    String PARAM_NAME_PAGE_SIZE = "pageSize";

    String ERR_MSG_FOLLOWER_ID_IS_NULL = "followerId 누락됨";
    String ERR_MSG_FOLLOWER_ID_OUT_OF_RANGE = "followerId 값 범위 오류: 0 또는 음수";
    String ERR_MSG_FOLLOWEE_ID_IS_NULL = "followeeId 누락됨";
    String ERR_MSG_FOLLOWEE_ID_OUT_OF_RANGE = "followeeId 값 범위 오류: 0 또는 음수";
    String ERR_MSG_FOLLOW_ID_IS_NULL = "followId 누락됨";
    String ERR_MSG_FOLLOW_ID_OUT_OF_RANGE = "followId 값 범위 오류: 0 또는 음수";
    String ERR_MSG_PAGE_CURSOR_OUT_OF_RANGE = "pageCursor 값 범위 오류: 음수";
    String ERR_MSG_PAGE_SIZE_OUT_OF_RANGE = "pageSize 값 범위 오류: 1 미만 또는 100 초과";
}
