package com.cinelog.controller;

import com.cinelog.dto.WatchlistItemDto;
import com.cinelog.dto.WatchlistRequest;
import com.cinelog.security.CustomUserPrincipal;
import com.cinelog.service.WatchlistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping("/api/v1/users/{userId}/watchlist")
    public List<WatchlistItemDto> getWatchlist(@PathVariable Long userId) {
        return watchlistService.getUserWatchlist(userId);
    }

    @PostMapping("/api/v1/watchlist")
    public ResponseEntity<WatchlistItemDto> addItem(@AuthenticationPrincipal CustomUserPrincipal principal,
                                                     @Valid @RequestBody WatchlistRequest request) {
        WatchlistItemDto dto = watchlistService.addToWatchlist(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/api/v1/watchlist/{movieId}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal CustomUserPrincipal principal,
                                            @PathVariable Long movieId) {
        watchlistService.removeFromWatchlist(principal.getId(), movieId);
        return ResponseEntity.noContent().build();
    }
}
