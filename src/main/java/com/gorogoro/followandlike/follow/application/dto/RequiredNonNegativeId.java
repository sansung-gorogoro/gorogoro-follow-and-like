package com.gorogoro.followandlike.follow.application.dto;

import java.util.Objects;

public record RequiredNonNegativeId(
        Long id
) {
    public RequiredNonNegativeId {
        Objects.requireNonNull(id, "id");
        if (id <= 0) {
            throw new IllegalArgumentException("id 는 0 또는 양수여야 합니다. id = " + id);
        }
    }

    public Long val() {
        return id;
    }
}
