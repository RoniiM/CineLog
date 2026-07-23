package com.cinelog.controller;

import com.cinelog.dto.WatchlistItemDto;
import com.cinelog.dto.WatchlistRequest;
import com.cinelog.service.WatchlistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/{userId}/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @PostMapping
    public ResponseEntity<WatchlistItemDto> addItem(@PathVariable Long userId, @Valid @RequestBody WatchlistRequest request) {
        WatchlistItemDto dto = watchlistService.addToWatchlist(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping
    public List<WatchlistItemDto> getWatchlist(@PathVariable Long userId) {
        return watchlistService.getUserWatchlist(userId);
    }

    @DeleteMapping("/{movieId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long userId, @PathVariable Long movieId) {
        watchlistService.removeFromWatchlist(userId, movieId);
        return ResponseEntity.noContent().build();
    }
}
