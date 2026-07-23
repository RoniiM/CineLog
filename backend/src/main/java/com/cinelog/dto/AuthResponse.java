package com.cinelog.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken
) {
}
