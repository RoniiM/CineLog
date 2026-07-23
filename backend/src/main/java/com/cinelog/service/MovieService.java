package com.cinelog.service;

import com.cinelog.dto.MovieDto;
import com.cinelog.dto.PageResponse;
import com.cinelog.dto.tmdb.TmdbMoviePageResponse;
import com.cinelog.dto.tmdb.TmdbMovieResponse;
import com.cinelog.entity.Movie;
import com.cinelog.exception.MovieNotFoundException;
import com.cinelog.integration.TmdbClient;
import com.cinelog.mapper.MovieMapper;
import com.cinelog.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MovieService {

    private static final Logger log = LoggerFactory.getLogger(MovieService.class);
    private static final Duration STALE_THRESHOLD = Duration.ofDays(7);

    private final MovieRepository movieRepository;
    private final TmdbClient tmdbClient;
    private final MovieMapper movieMapper;

    public MovieService(MovieRepository movieRepository, TmdbClient tmdbClient, MovieMapper movieMapper) {
        this.movieRepository = movieRepository;
        this.tmdbClient = tmdbClient;
        this.movieMapper = movieMapper;
    }

    @Transactional
    public MovieDto getMovieDetails(Long tmdbId) {
        Optional<Movie> existing = movieRepository.findByTmdbId(tmdbId);

        if (existing.isPresent() && !isStale(existing.get())) {
            log.info("Serving movie details from local database, tmdbId={}", tmdbId);
            return movieMapper.toDto(existing.get());
        }

        TmdbMovieResponse response = tmdbClient.fetchMovieDetails(tmdbId);
        if (response == null || response.id() == null) {
            throw new MovieNotFoundException("Movie not found on TMDB, tmdbId=" + tmdbId);
        }

        Movie movie = existing.orElse(null);
        if (movie != null) {
            log.info("Refreshing stale movie metadata, tmdbId={}", tmdbId);
            movieMapper.updateEntityFromResponse(response, movie);
        } else {
            log.info("Creating new movie record, tmdbId={}", tmdbId);
            movie = movieMapper.toEntity(response);
        }
        movie.setLastSynced(Instant.now());
        movieRepository.save(movie);
        log.info("Synchronized movie metadata, tmdbId={}", tmdbId);

        return movieMapper.toDto(response);
    }

    public PageResponse<MovieDto> searchMovies(String query, int page) {
        log.info("Searching movies, query='{}', page={}", query, page);
        TmdbMoviePageResponse response = tmdbClient.searchMovies(query, page);
        return toPageResponse(response);
    }

    public PageResponse<MovieDto> discoverMovies(Map<String, String> filters, int page) {
        log.info("Discovering movies, page={}, filters={}", page, filters);
        TmdbMoviePageResponse response = tmdbClient.discoverMovies(filters, page);
        return toPageResponse(response);
    }

    private PageResponse<MovieDto> toPageResponse(TmdbMoviePageResponse response) {
        List<MovieDto> content = response.results() == null
                ? List.of()
                : response.results().stream().map(movieMapper::toDto).toList();
        return new PageResponse<>(
                content,
                response.page() != null ? response.page() : 0,
                response.totalPages() != null ? response.totalPages() : 0,
                response.totalResults() != null ? response.totalResults() : 0
        );
    }

    private boolean isStale(Movie movie) {
        return movie.getLastSynced() == null
                || movie.getLastSynced().isBefore(Instant.now().minus(STALE_THRESHOLD));
    }
}
