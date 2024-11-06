package com.movieapp.movie_api.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequest (@NotBlank String email,
                            @NotBlank String password) {}
