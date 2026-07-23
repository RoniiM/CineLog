package com.cinelog.dto;

import java.time.LocalDate;

public record MovieDto(
        Long id,
        Long tmdbId,
        String title,
        String overview,
        String posterUrl,
        String backdropUrl,
        LocalDate releaseDate
) {
}
