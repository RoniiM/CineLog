package com.cinelog.controller;

import com.cinelog.dto.MovieDto;
import com.cinelog.dto.PageResponse;
import com.cinelog.service.MovieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/{tmdbId}")
    public MovieDto getMovieDetails(@PathVariable Long tmdbId) {
        return movieService.getMovieDetails(tmdbId);
    }

    @GetMapping("/search")
    public PageResponse<MovieDto> searchMovies(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page) {
        return movieService.searchMovies(query, page);
    }

    @GetMapping("/discover")
    public PageResponse<MovieDto> discoverMovies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam Map<String, String> filters) {
        filters.remove("page");
        return movieService.discoverMovies(filters, page);
    }
}
