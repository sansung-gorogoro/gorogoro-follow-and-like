package com.gorogoro.followandlike.follow.presentation.response;

public record CountResponse (
        Long count
) {
    public static CountResponse of(Long count) {
        return new CountResponse(count);
    }
}
