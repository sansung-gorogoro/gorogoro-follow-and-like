package com.gorogoro.followandlike.common.application.dto;

import java.util.List;

public record CursorPageResult<T> (
        List<T> content,
        Long nextCursor,
        Boolean hasNext
) {
}
