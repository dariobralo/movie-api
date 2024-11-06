package com.movieapp.movie_api.auth.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse (Integer userId,
                            String username,
                            String tokenType,
                            String accessToken,
                            Long expiresIn,
                            String refreshToken){}
