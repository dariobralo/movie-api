package com.movieapp.movie_api.auth.service;

import com.movieapp.movie_api.auth.dto.UserDto;
import com.movieapp.movie_api.auth.dto.request.LogoutRequest;
import com.movieapp.movie_api.auth.entity.RefreshToken;
import com.movieapp.movie_api.auth.entity.User;
import com.movieapp.movie_api.auth.entity.UserRoleEnum;
import com.movieapp.movie_api.auth.repository.UserRepository;
import com.movieapp.movie_api.auth.dto.response.AuthResponse;
import com.movieapp.movie_api.auth.dto.request.LoginRequest;
import com.movieapp.movie_api.auth.dto.request.RegisterRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    /*
     *
     */
    public UserDto register(RegisterRequest registerRequest){
        if(userRepository.existsByEmail(registerRequest.email())){
            throw new DataIntegrityViolationException(
                    "El email ingresado no esta disponible");
        }
        if(userRepository.existsByNickName(registerRequest.username())){
            throw new DataIntegrityViolationException(
                    "El nombre de usuario seleccionado no esta disponible");
        }

        var user = User.builder()
                .email(registerRequest.email())
                .nickName(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .role(UserRoleEnum.USER)
                .build();

        User savedUser = userRepository.save(user);

        return UserDto.builder()
                .email(savedUser.getEmail())
                .username(savedUser.getNickName())
                .role(savedUser.getRole().toString())
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest){
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.email(),
                            loginRequest.password()
                    )
            );

            User user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Error de conexion con base de datos intente más tarde."));
            if (user.getRefreshToken()!= null){
                throw new ValidationException("Session ya iniciada. Cierre sesion para volver a loguearse.");
            }

            if (authentication.isAuthenticated()){
                String email = authentication.getName();
                UserDetails userDetails= (UserDetails) authentication.getPrincipal();

                var accessToken = jwtService.generateToken(userDetails);
                var refreshToken = refreshTokenService.createRefreshToken(email);

                return AuthResponse.builder()
                        .username(email)
                        .tokenType("Bearer ")
                        .accessToken(accessToken)
                        .expiresIn(jwtService.extractExpiration(accessToken).getTime())
                        .refreshToken(refreshToken.getRefreshToken())
                        .build();
            } else {
                throw new BadCredentialsException("Datos ingresados incorrectos");
            }
    }

//    @Transactional
    public AuthResponse refreshJwt(String refreshToken){
        RefreshToken refreshToken1 = refreshTokenService.verifyRefreshToken(refreshToken);

        if (refreshToken1 == null){
            throw new BadCredentialsException(
                    "No se pudo validar el RefreshToken, por favor vuelva a iniciar sesion.");
        }

        User user = refreshToken1.getUser();
        String accessToken = jwtService.generateToken(user);

        refreshTokenService.deleteRefreshToken(refreshToken1);

        refreshToken1 = refreshTokenService.createRefreshToken(user.getUsername());

        return AuthResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .tokenType("Bearer")
                .accessToken(accessToken)
                .expiresIn(jwtService.extractExpiration(accessToken).getTime())
                .refreshToken(refreshToken1.getRefreshToken())
                .build();
    }

    public void logout(LogoutRequest request){
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new UsernameNotFoundException("Email no válido"));

            RefreshToken token = user.getRefreshToken();
            if(token == null){
                throw new BadCredentialsException("No hay sesión activa");
            }

            refreshTokenService.deleteRefreshToken(token);
    }

}
