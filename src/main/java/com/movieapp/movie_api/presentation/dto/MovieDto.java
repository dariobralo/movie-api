package com.movieapp.movie_api.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record MovieDto (long movieId,
                        @NotBlank(message = "Movie title is required")
                        String title,
                        @NotBlank(message = "Director's name is required")
                        String director,
                        Set<String> movieCast,
                        @NotNull(message = "El año de lanzamiento no puede estar vacio.")
                        Integer releaseYear,
                        String imageFileName,
                        String imageUrl){
}
