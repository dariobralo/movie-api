package com.movieapp.movie_api.presentation.controller;

import com.movieapp.movie_api.auth.dto.request.ChangePasswordRequest;
import com.movieapp.movie_api.auth.dto.request.EmailVerifyRequest;
import com.movieapp.movie_api.auth.dto.request.OtpVerifyRequest;
import com.movieapp.movie_api.auth.dto.response.PasswordResetTokenResponse;
import com.movieapp.movie_api.auth.service.JwtService;
import com.movieapp.movie_api.auth.service.PasswordResetService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/resetPassword")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final JwtService jwtService;

    public PasswordResetController(PasswordResetService passwordResetService, JwtService jwtService) {
        this.passwordResetService = passwordResetService;
        this.jwtService = jwtService;
    }

    /**
     * Envia un código de verificación al correo del usuario para comenzar con el proceso de
     * reestablecimiento de contraseña.
     *
     * @param email El objeto EmailVerifyRequest recibe el email del usuario.
     * @return código de respuesta 200 al enviarse con exito el codigo al email de usuario, un
     *         404 not found en caso de no encontrarse el usuario en la base de datos o un 400
     *         en caso de enviar datos en un formato que no sea un email válido.
     */
    @PostMapping("/verifyEmail")
    public ResponseEntity<String> sendOtpToEmail(@Valid @RequestBody EmailVerifyRequest email){
        passwordResetService.verifyEmail(email);

        return ResponseEntity.ok().body("Código de recuperación de cuenta enviado a " + email.email() + ".");
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<PasswordResetTokenResponse> verifyOtp(@Valid @RequestBody
                                                OtpVerifyRequest request){

            boolean isValid = passwordResetService.verifyOtp(request);

            if(isValid){
                String passwordResetToken = jwtService.generatePasswordResetToken(request.email());
                PasswordResetTokenResponse response = PasswordResetTokenResponse
                        .builder()
                        .token(passwordResetToken)
                        .build();

                return ResponseEntity.ok().body(response);
            } else {
                throw new BadCredentialsException("Código incorrecto.");
            }
    }

    @PostMapping("/changePassword")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request){
        if (request.newPassword().equals(request.repeatNewPassword())){
            passwordResetService.changePassword(request);

            return ResponseEntity.ok("Contraseña actualizada.");
        }
        return ResponseEntity.badRequest().body("Las contraseñas deben coincidir.");
    }

}

/*
    @PostMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@RequestBody ChangePassword changePassword,
                                                        @PathVariable String email) {
        if (!Objects.equals(changePassword.password(), changePassword.repeatPassword())) {
            return new ResponseEntity<>("Please enter the password again!", HttpStatus.EXPECTATION_FAILED);
        }
        String encodedPassword = passwordEncoder.encode(changePassword.password());
        userRepository.updatePassword(email, encodedPassword);
        return ResponseEntity.ok("Password has been changed!");
    }
 */