package com.cinelog.dto;

import java.time.Instant;

public record WatchlistItemDto(
        Long id,
        MovieDto movie,
        Instant addedAt
) {
}
