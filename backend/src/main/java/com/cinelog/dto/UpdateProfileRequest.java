package com.cinelog.dto;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(max = 1000) String bio,
        String avatarUrl
) {
}
