package com.gorogoro.followandlike.follow.domain.model.dto;

import lombok.NonNull;

import java.util.List;

public record CursorBasedPaginatedResult<T> (
        @NonNull List<T> content,
        Long nextCursor,
        Boolean hasNext
) {
}
