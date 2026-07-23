package com.cinelog.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TmdbMoviePageResponse(
        Integer page,
        @JsonProperty("total_pages") Integer totalPages,
        @JsonProperty("total_results") Integer totalResults,
        List<TmdbMovieResponse> results
) {
}
