package com.cinelog.integration;

import com.cinelog.dto.tmdb.TmdbMoviePageResponse;
import com.cinelog.dto.tmdb.TmdbMovieResponse;
import com.cinelog.exception.ExternalApiException;
import com.cinelog.exception.MovieNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Component
public class TmdbClient {

    private static final Logger log = LoggerFactory.getLogger(TmdbClient.class);

    private final RestClient tmdbRestClient;
    private final String apiKey;

    public TmdbClient(RestClient tmdbRestClient, @Value("${tmdb.api.key}") String apiKey) {
        this.tmdbRestClient = tmdbRestClient;
        this.apiKey = apiKey;
    }

    @Cacheable(value = "movieDetails", key = "#tmdbId")
    public TmdbMovieResponse fetchMovieDetails(Long tmdbId) {
        log.info("Requesting movie details from TMDB (cache miss), tmdbId={}", tmdbId);
        try {
            return tmdbRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/movie/{id}")
                            .queryParam("api_key", apiKey)
                            .build(tmdbId))
                    .retrieve()
                    .body(TmdbMovieResponse.class);
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("TMDB reported movie not found, tmdbId={}", tmdbId);
            throw new MovieNotFoundException("Movie not found on TMDB, tmdbId=" + tmdbId);
        } catch (RestClientException ex) {
            log.error("TMDB request failed while fetching movie details, tmdbId={}", tmdbId, ex);
            throw new ExternalApiException("Failed to fetch movie details from TMDB", ex);
        }
    }

    @Cacheable(value = "movieSearch", key = "#query + '-' + #page")
    public TmdbMoviePageResponse searchMovies(String query, int page) {
        log.info("Searching TMDB movies (cache miss), query='{}', page={}", query, page);
        try {
            return tmdbRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/search/movie")
                            .queryParam("api_key", apiKey)
                            .queryParam("query", query)
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .body(TmdbMoviePageResponse.class);
        } catch (RestClientException ex) {
            log.error("TMDB request failed while searching movies, query='{}', page={}", query, page, ex);
            throw new ExternalApiException("Failed to search movies on TMDB", ex);
        }
    }

    public TmdbMoviePageResponse discoverMovies(Map<String, String> filters, int page) {
        log.info("Discovering TMDB movies, page={}, filters={}", page, filters);
        try {
            return tmdbRestClient.get()
                    .uri(uriBuilder -> {
                        uriBuilder.path("/discover/movie")
                                .queryParam("api_key", apiKey)
                                .queryParam("page", page);
                        filters.forEach(uriBuilder::queryParam);
                        return uriBuilder.build();
                    })
                    .retrieve()
                    .body(TmdbMoviePageResponse.class);
        } catch (RestClientException ex) {
            log.error("TMDB request failed while discovering movies, page={}, filters={}", page, filters, ex);
            throw new ExternalApiException("Failed to discover movies on TMDB", ex);
        }
    }
}
