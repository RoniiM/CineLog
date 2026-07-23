package com.cinelog.dto;

import java.time.LocalDate;

public record MovieDto(
        Long tmdbId,
        String title,
        String overview,
        String posterUrl,
        String backdropUrl,
        LocalDate releaseDate
) {
}
