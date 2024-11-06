package com.movieapp.movie_api.auth.dto;

import lombok.Builder;

@Builder
public record UserDto (String email,
                       String username,
                       String role){
}
