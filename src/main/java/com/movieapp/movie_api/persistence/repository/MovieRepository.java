package com.movieapp.movie_api.persistence.repository;

import com.movieapp.movie_api.persistence.entity.MovieEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<MovieEntity, Long> {
}
