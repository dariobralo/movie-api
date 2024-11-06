package com.movieapp.movie_api.presentation.dto.response;

import com.movieapp.movie_api.presentation.dto.MovieDto;

import java.util.List;

public record MoviePageResponse(List<MovieDto> movieDtoList,
                                Integer pageNumber,
                                Integer pageSize,
                                long totalElements,
                                int totalPages,
                                boolean isLast) {
}
