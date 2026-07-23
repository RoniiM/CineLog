package com.cinelog.dto;

import jakarta.validation.constraints.NotNull;

public record WatchlistRequest(
        @NotNull Long tmdbId
) {
}
