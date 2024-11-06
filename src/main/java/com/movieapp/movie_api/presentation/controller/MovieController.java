package com.movieapp.movie_api.presentation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieapp.movie_api.exception.EmptyFileException;
import com.movieapp.movie_api.exception.MovieCreationException;
import com.movieapp.movie_api.presentation.dto.MovieDto;
import com.movieapp.movie_api.presentation.dto.response.MoviePageResponse;
import com.movieapp.movie_api.service.FileService;
import com.movieapp.movie_api.service.MovieService;
import com.movieapp.movie_api.util.constants.AppConstants;
import jakarta.annotation.Resource;
import jakarta.validation.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieService movieService;
    private final FileService fileService;

    public MovieController(MovieService movieService, FileService fileService){
        this.movieService = movieService;
        this.fileService = fileService;
    }

    /*
     *
     */
    @GetMapping("/{movieId}")
    public ResponseEntity<MovieDto> getMovieHandler(@PathVariable Long movieId){
        return ResponseEntity.ok(movieService.getMovie(movieId));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<MovieDto>> getAllMoviesHandler(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/movies-page")
    public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize

    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPagination(pageNumber, pageSize));
    }

    @GetMapping("/movies-page-and-sort")
    public ResponseEntity<MoviePageResponse> getMoviesWithPagesAndSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) int pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIRECCION, required = false) String direccion
    ){
        return ResponseEntity.ok(movieService.getAllMoviesWithPaginationAndSorting(pageNumber, pageSize, sortBy, direccion));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/new-movie", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MovieDto> addNewMovieHandler(@RequestPart("file") MultipartFile file,
                                                       @RequestPart("movie") String movieDto) throws EmptyFileException, JsonProcessingException {

        if (!isValidImage(file)){
            throw new EmptyFileException("La imagen de la película es requerida");
        }

        MovieDto requestDto = convertToMovieDto(movieDto);
        MovieDto newMovie = movieService.addMovie(requestDto, file);

        if (newMovie == null){
            throw new MovieCreationException("Error al crear pelicula.");
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{movieId}")
                .buildAndExpand(newMovie.movieId())
                .toUri();

        return ResponseEntity.created(location).body(newMovie);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMovieHandler(@PathVariable Long movieId,
                                                       @RequestPart("movie") String stringMovieDto,
                                                       @RequestPart("file") MultipartFile file
                                                        ) throws IOException {
        MovieDto movieDto = convertToMovieDto(stringMovieDto);

        if (file.isEmpty()) file = null;

        MovieDto updatedMovie = movieService.updateMovie(movieId, movieDto, file);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{movieId}")
                .buildAndExpand(updatedMovie.movieId())
                .toUri();

        return ResponseEntity.created(location).body(updatedMovie);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/images/update/{movieId}")
    public ResponseEntity<Resource> uploadImage(@RequestPart MultipartFile file,
                                                @PathVariable Long movieId){
        return null;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Long movieId) throws IOException {
        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    /*
     *
     */
    private MovieDto convertToMovieDto(String stringMovieDto) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        MovieDto movieDto = objectMapper.readValue(stringMovieDto, MovieDto.class);

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<MovieDto>> violations = validator.validate(movieDto);

        if (!violations.isEmpty()){
            throw new ValidationException("Error en la validación de los datos recibidos.");
        }

        return movieDto;
    }

    private boolean isValidImage(MultipartFile file){
        try{
            BufferedImage imagen = ImageIO.read(file.getInputStream());
            return imagen != null;
        } catch (IOException e) {
            return false;
        }
    }

}