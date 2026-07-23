package com.cinelog.service;

import com.cinelog.dto.DiaryEntryDto;
import com.cinelog.dto.DiaryEntryRequest;
import com.cinelog.entity.DiaryEntry;
import com.cinelog.entity.Movie;
import com.cinelog.entity.User;
import com.cinelog.exception.DiaryEntryNotFoundException;
import com.cinelog.exception.MovieNotFoundException;
import com.cinelog.exception.UserNotFoundException;
import com.cinelog.mapper.DiaryMapper;
import com.cinelog.repository.DiaryEntryRepository;
import com.cinelog.repository.MovieRepository;
import com.cinelog.repository.UserRepository;
import com.cinelog.repository.WatchlistItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiaryService {

    private static final Logger log = LoggerFactory.getLogger(DiaryService.class);

    private final DiaryEntryRepository diaryEntryRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final MovieService movieService;
    private final DiaryMapper diaryMapper;
    private final WatchlistItemRepository watchlistItemRepository;

    public DiaryService(DiaryEntryRepository diaryEntryRepository, UserRepository userRepository,
                         MovieRepository movieRepository, MovieService movieService, DiaryMapper diaryMapper,
                         WatchlistItemRepository watchlistItemRepository) {
        this.diaryEntryRepository = diaryEntryRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.movieService = movieService;
        this.diaryMapper = diaryMapper;
        this.watchlistItemRepository = watchlistItemRepository;
    }

    @Transactional
    public DiaryEntryDto addDiaryEntry(Long userId, DiaryEntryRequest request) {
        User user = findUserOrThrow(userId);
        movieService.getMovieDetails(request.tmdbId());
        Movie movie = movieRepository.findByTmdbId(request.tmdbId())
                .orElseThrow(() -> new MovieNotFoundException("Movie not found, tmdbId=" + request.tmdbId()));

        if (watchlistItemRepository.existsByUserIdAndMovieId(userId, movie.getId())) {
            watchlistItemRepository.deleteByUserIdAndMovieId(userId, movie.getId());
            log.info("Removed movie from watchlist after logging, userId={}, tmdbId={}", userId, request.tmdbId());
        }

        DiaryEntry entry = DiaryEntry.builder()
                .user(user)
                .movie(movie)
                .watchedDate(request.watchedDate())
                .rating(request.rating())
                .review(request.review())
                .build();

        DiaryEntry saved = diaryEntryRepository.save(entry);
        log.info("Added diary entry, userId={}, tmdbId={}", userId, request.tmdbId());
        return diaryMapper.toDto(saved);
    }

    public List<DiaryEntryDto> getUserDiary(Long userId) {
        findUserOrThrow(userId);
        return diaryEntryRepository.findByUserIdOrderByWatchedDateDesc(userId).stream()
                .map(diaryMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteDiaryEntry(Long userId, Long entryId) {
        findUserOrThrow(userId);
        DiaryEntry entry = diaryEntryRepository.findById(entryId)
                .filter(e -> e.getUser().getId().equals(userId))
                .orElseThrow(() -> new DiaryEntryNotFoundException("Diary entry not found, id=" + entryId));
        diaryEntryRepository.delete(entry);
        log.info("Deleted diary entry, userId={}, entryId={}", userId, entryId);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found, id=" + userId));
    }
}
