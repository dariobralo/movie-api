package com.movieapp.movie_api.auth.service;

import com.movieapp.movie_api.auth.entity.RefreshToken;
import com.movieapp.movie_api.auth.entity.User;
import com.movieapp.movie_api.auth.repository.RefreshTokenRepository;
import com.movieapp.movie_api.auth.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final Integer EXPIRATION_REFRESH_TOKEN_TIME = 1000 * 60 * 60;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /*
     *
     */
    public RefreshToken createRefreshToken(String username){
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con email '[%s]' no encontrado".formatted(username)));

        RefreshToken refreshToken = user.getRefreshToken();

        if (refreshToken == null){
            long refreshTokenValidity = EXPIRATION_REFRESH_TOKEN_TIME;

            refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();
            refreshTokenRepository.save(refreshToken);
        }

        return refreshToken;
    }

    public RefreshToken verifyRefreshToken(String refreshToken){
        RefreshToken refToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Refresh Token no encontrado. " +
                        "Verifique los datos o vuelva a iniciar sesion."));

        if (refToken.getExpirationTime().compareTo(Instant.now()) < 0){
            this.deleteRefreshToken(refToken);
            throw new BadCredentialsException("Refresh Token expirado. Vuelva a iniciar sesión.");
        }

        return refToken;
    }

    public void deleteRefreshToken(RefreshToken refreshToken){
        User user = refreshToken.getUser();
        if (user!=null)
            user.setRefreshToken(null);

        int tokenId = refreshToken.getTokenId();

        refreshTokenRepository.delete(refreshToken);
        if(refreshTokenRepository.existsById(tokenId)){
            throw new RuntimeException("Error interno al intentar realizar la accion solicitada. " +
                    "Por favor intente de nuevo o reporte el error.");
        }
    }

}
