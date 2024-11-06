package com.movieapp.movie_api.util.mapper;

import com.movieapp.movie_api.persistence.entity.MovieEntity;
import com.movieapp.movie_api.presentation.dto.MovieDto;
import com.movieapp.movie_api.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class MovieDtoMapper implements Function<MovieEntity, MovieDto> {

    private final FileService fileService;
    private final String imageEndpoint;

    public MovieDtoMapper(FileService fileService ,@Value("${image.endpoint}") String imageEndpoint){
        this.fileService = fileService;
        this.imageEndpoint = imageEndpoint;
    }

    @Override
    public MovieDto apply(MovieEntity movieEntity) {
        return new MovieDto(
                movieEntity.getMovieId(),
                movieEntity.getTitle(),
                movieEntity.getDirector(),
                movieEntity.getMovieCast(),
                movieEntity.getReleaseYear(),
                movieEntity.getImageFileName(),
                imageEndpoint + movieEntity.getImageFileName()
        );
    }

}
