package com.cinelog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record DiaryEntryRequest(
        @NotNull Long tmdbId,
        @NotNull @PastOrPresent LocalDate watchedDate,
        @NotNull @Min(1) @Max(5) Integer rating,
        String review
) {
}
