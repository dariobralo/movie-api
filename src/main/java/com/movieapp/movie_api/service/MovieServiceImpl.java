package com.movieapp.movie_api.service;

import com.movieapp.movie_api.exception.MovieCreationException;
import com.movieapp.movie_api.exception.MovieNotFoundException;
import com.movieapp.movie_api.persistence.entity.MovieEntity;
import com.movieapp.movie_api.persistence.repository.MovieRepository;
import com.movieapp.movie_api.presentation.dto.MovieDto;
import com.movieapp.movie_api.presentation.dto.response.MoviePageResponse;
import com.movieapp.movie_api.util.mapper.MovieDtoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;
    private final FileService fileService;
    private final MovieDtoMapper movieDtoMapper;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService, MovieDtoMapper movieDtoMapper){
        this.movieRepository= movieRepository;
        this.fileService = fileService;
        this.movieDtoMapper = movieDtoMapper;
    }

    /*
     *
     */
    @Override
    public MovieDto getMovie(Long movieId) {
        return movieRepository.findById(movieId)
                .map(movieDtoMapper)
                .orElseThrow(() -> new MovieNotFoundException(
                        "Pelicula con id [%s] no encontrada".formatted(movieId)
                ));
    }

    @Override
    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(movieDtoMapper)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MovieDto addMovie(MovieDto movieDto, MultipartFile file) throws MovieCreationException {

        String uploadedFileName = fileService.uploadFile(movieDto.title(), file);

        MovieEntity movieEntity = movieRepository.save(new MovieEntity(
                null,
                movieDto.title(),
                movieDto.director(),
                movieDto.movieCast(),
                movieDto.releaseYear(),
                uploadedFileName
        ));

        return movieRepository.findById(movieEntity.getMovieId())
                .map(movieDtoMapper)
                .orElseThrow(() -> new MovieNotFoundException(
                        "Ha ocurrido un error al buscar pelicula guardada."
                ));
    }

    @Override
    @Transactional
    public MovieDto updateMovie(Long movieId, MovieDto movieDto, MultipartFile file) throws IOException {
        MovieEntity movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(
                        "Pelicula con id = [%s] no encontrada".formatted(movieId)
                ));
        String newFileName = null;

        if (file != null){
            fileService.deleteFile(movie.getImageFileName());
            newFileName = fileService.uploadFile(movie.getTitle(), file);
        }

        movie.setTitle(movieDto.title());
        movie.setDirector(movieDto.director());
        movie.setMovieCast(movieDto.movieCast());
        movie.setReleaseYear(movie.getReleaseYear());
        if (newFileName!=null) movie.setImageFileName(newFileName);


        return movieDtoMapper.apply(movieRepository.save(movie));
    }

    @Override
    public String deleteMovie(Long movieId) throws IOException {
        MovieEntity movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new MovieNotFoundException(
                        "Pelicula con id [%s] no ha sido encontrada".formatted(movieId))
                );

        fileService.deleteFile(movie.getImageFileName());
        movieRepository.delete(movie);

        return "Pelicula con id = " + movieId + " eliminada.";
    }

    @Override
    public MoviePageResponse getAllMoviesWithPagination(Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<MovieEntity> movieEntityPage = movieRepository.findAll(pageable);
        List<MovieEntity> movieList = movieEntityPage.getContent();
        List<MovieDto> movieDtoList = new ArrayList<>();

        for (MovieEntity movie : movieList){
            MovieDto movieDto = movieDtoMapper.apply(movie);
            movieDtoList.add(movieDto);
        }

        return new MoviePageResponse(
                movieDtoList, pageNumber, pageSize,
                movieEntityPage.getTotalElements(),
                movieEntityPage.getTotalPages(),
                movieEntityPage.isLast()
        );
    }

    @Override
    public MoviePageResponse getAllMoviesWithPaginationAndSorting(Integer pageNumber, Integer pageSize,
                                                                  String sortBy, String direccion) {
        Sort sort = direccion.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
                                                                    : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<MovieEntity> movieEntityPage = movieRepository.findAll(pageable);
        List<MovieEntity> movieEntityList = movieEntityPage.getContent();
        List<MovieDto> movieDtoList = new ArrayList<>();

        for (MovieEntity movie : movieEntityList){
            MovieDto movieDto = movieDtoMapper.apply(movie);
            movieDtoList.add(movieDto);
        }

        return new MoviePageResponse(movieDtoList, pageNumber, pageSize,
                movieEntityPage.getTotalElements(),
                movieEntityPage.getTotalPages(),
                movieEntityPage.isLast());
    }

}