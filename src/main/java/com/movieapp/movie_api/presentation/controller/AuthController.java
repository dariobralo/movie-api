package com.movieapp.movie_api.presentation.controller;

import com.movieapp.movie_api.auth.dto.UserDto;
import com.movieapp.movie_api.auth.dto.request.LogoutRequest;
import com.movieapp.movie_api.auth.dto.response.AuthResponse;
import com.movieapp.movie_api.auth.dto.request.LoginRequest;
import com.movieapp.movie_api.auth.dto.request.RefreshTokenRequest;
import com.movieapp.movie_api.auth.dto.request.RegisterRequest;
import com.movieapp.movie_api.auth.entity.RefreshToken;
import com.movieapp.movie_api.auth.entity.User;
import com.movieapp.movie_api.auth.service.AuthService;
import com.movieapp.movie_api.auth.service.JwtService;
import com.movieapp.movie_api.auth.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    /*
     *
     */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody
                                                RegisterRequest registerRequest){
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody
                                                  LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody
                                                         RefreshTokenRequest refreshTokenRequest){
        return ResponseEntity.ok().body(authService.refreshJwt(refreshTokenRequest.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody
                                             LogoutRequest logoutRequest){
        authService.logout(logoutRequest);

        return ResponseEntity.ok("Sesion cerrada.");
    }

}
