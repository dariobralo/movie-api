package com.movieapp.movie_api.service;

import com.movieapp.movie_api.exception.MovieCreationException;
import com.movieapp.movie_api.exception.MovieNotFoundException;
import com.movieapp.movie_api.presentation.dto.MovieDto;
import com.movieapp.movie_api.presentation.dto.response.MoviePageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MovieService {

    MovieDto getMovie(Long movieId) throws MovieNotFoundException;

    List<MovieDto> getAllMovies();

    MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws MovieCreationException;

    MovieDto updateMovie(Long movieId, MovieDto movieDto, MultipartFile file) throws IOException;

    String deleteMovie(Long movieId) throws IOException;

    MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize);

    MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize, String sortBy, String direccion);

}
