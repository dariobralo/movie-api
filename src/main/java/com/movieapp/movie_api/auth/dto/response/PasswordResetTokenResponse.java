package com.movieapp.movie_api.auth.dto.response;

import lombok.Builder;

@Builder
public record PasswordResetTokenResponse(
        String token
) {
}
