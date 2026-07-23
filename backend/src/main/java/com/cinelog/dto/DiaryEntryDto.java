package com.cinelog.dto;

import java.time.Instant;
import java.time.LocalDate;

public record DiaryEntryDto(
        Long id,
        MovieDto movie,
        LocalDate watchedDate,
        Integer rating,
        String review,
        Instant createdAt
) {
}
