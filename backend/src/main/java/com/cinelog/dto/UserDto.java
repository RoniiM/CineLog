package com.cinelog.dto;

import java.time.Instant;

public record UserDto(
        Long id,
        String username,
        String email,
        String bio,
        String avatarUrl,
        Instant createdAt
) {
}
