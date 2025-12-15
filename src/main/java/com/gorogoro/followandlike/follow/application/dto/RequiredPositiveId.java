package com.gorogoro.followandlike.follow.application.dto;

import java.util.Objects;

public record RequiredPositiveId (
        Long id
) {
    public RequiredPositiveId {
        Objects.requireNonNull(id, "id");
        if (id < 0) {
            throw new IllegalArgumentException("id 는 양수여야 합니다. id = " + id);
        }
    }

    public Long val() {
        return id;
    }
}
