package com.gorogoro.followandlike.follow.application.dto;

import java.util.List;

public record CursorBasedPaginatedResponse<T> (
        List<T> content,
        Long nextCursor,
        Boolean hasNext
) {
}
