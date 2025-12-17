package com.gorogoro.followandlike.common.presentation;

public interface CommonHttpConstants {
    public static final String PARAM_NAME_X_USER_ID = "X-User-Id";

    public static final String ERR_MSG_USER_ID_IS_NULL = "요청자 id 누락됨";
    public static final String ERR_MSG_USER_ID_OUT_OF_RANGE = "요청자 id 값 범위 오류: 0 또는 음수";
}
