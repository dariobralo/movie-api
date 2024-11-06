package com.movieapp.movie_api.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "peliculas")
public class MovieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;

    @Column(nullable = false, length = 170)
    @NotBlank(message = "El título de la película no puede estar vacío.")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Indicar nombre del director.")
    private String director;

    @ElementCollection
    @CollectionTable(name = "reparto")
    private Set<String> movieCast;

    @Column(nullable = false)
    @NotNull(message = "Es necesario especificar año de lanzamiento.")
    private Integer releaseYear;

    @Column(nullable = false)
    @NotBlank(message = "La imagen de la película no puede estar vacía.")
    private String imageFileName;

}
