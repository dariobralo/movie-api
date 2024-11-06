package com.movieapp.movie_api.auth.repository;

import com.movieapp.movie_api.auth.entity.PasswordReset;
import com.movieapp.movie_api.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository <PasswordReset, Long> {

    @Query("SELECT pr FROM PasswordReset pr WHERE pr.otp = ?1 and pr.user = ?2")
    Optional<PasswordReset> findByOtpAndUser(Integer otp, User user);

}
