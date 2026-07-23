package com.cinelog.service;

import com.cinelog.dto.WatchlistItemDto;
import com.cinelog.dto.WatchlistRequest;
import com.cinelog.entity.Movie;
import com.cinelog.entity.User;
import com.cinelog.entity.WatchlistItem;
import com.cinelog.exception.DuplicateWatchlistException;
import com.cinelog.exception.MovieNotFoundException;
import com.cinelog.exception.UserNotFoundException;
import com.cinelog.mapper.WatchlistMapper;
import com.cinelog.repository.MovieRepository;
import com.cinelog.repository.UserRepository;
import com.cinelog.repository.WatchlistItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WatchlistService {

    private static final Logger log = LoggerFactory.getLogger(WatchlistService.class);

    private final WatchlistItemRepository watchlistItemRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final WatchlistMapper watchlistMapper;

    public WatchlistService(WatchlistItemRepository watchlistItemRepository, UserRepository userRepository,
                             MovieRepository movieRepository, MovieService movieService, WatchlistMapper watchlistMapper) {
        this.watchlistItemRepository = watchlistItemRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.movieService = movieService;
        this.watchlistMapper = watchlistMapper;
    }

    @Transactional
    public WatchlistItemDto addToWatchlist(Long userId, WatchlistRequest request) {
        User user = findUserOrThrow(userId);
        movieService.getMovieDetails(request.tmdbId());
        Movie movie = movieRepository.findByTmdbId(request.tmdbId())
                .orElseThrow(() -> new MovieNotFoundException("Movie not found, tmdbId=" + request.tmdbId()));

        if (watchlistItemRepository.existsByUserIdAndMovieId(userId, movie.getId())) {
            throw new DuplicateWatchlistException("Movie already in watchlist, tmdbId=" + request.tmdbId());
        }

        WatchlistItem item = WatchlistItem.builder()
                .user(user)
                .movie(movie)
                .build();

        WatchlistItem saved = watchlistItemRepository.save(item);
        log.info("Added movie to watchlist, userId={}, tmdbId={}", userId, request.tmdbId());
        return watchlistMapper.toDto(saved);
    }

    public List<WatchlistItemDto> getUserWatchlist(Long userId) {
        findUserOrThrow(userId);
        return watchlistItemRepository.findByUserId(userId).stream()
                .map(watchlistMapper::toDto)
                .toList();
    }

    @Transactional
    public void removeFromWatchlist(Long userId, Long movieId) {
        findUserOrThrow(userId);
        watchlistItemRepository.deleteByUserIdAndMovieId(userId, movieId);
        log.info("Removed movie from watchlist, userId={}, movieId={}", userId, movieId);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found, id=" + userId));
    }
}
