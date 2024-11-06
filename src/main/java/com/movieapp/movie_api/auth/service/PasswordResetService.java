package com.movieapp.movie_api.auth.service;

import com.movieapp.movie_api.auth.dto.request.ChangePasswordRequest;
import com.movieapp.movie_api.auth.dto.request.EmailVerifyRequest;
import com.movieapp.movie_api.auth.dto.request.OtpVerifyRequest;
import com.movieapp.movie_api.auth.dto.response.MailBodyResponse;
import com.movieapp.movie_api.auth.entity.PasswordReset;
import com.movieapp.movie_api.auth.entity.User;
import com.movieapp.movie_api.auth.repository.PasswordResetRepository;
import com.movieapp.movie_api.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private static final Integer OTP_EXPIRATION_TIME = 1000 * 60 * 5; //5 minutos

    /*
     *
     */
    @Transactional(rollbackFor = {RuntimeException.class})
    public void verifyEmail(EmailVerifyRequest emailRequest) {
        User user = userRepository.findByEmail(emailRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "El email ingresado no corresponde a un usuario registrado."));
        long id = 0;

        if (user.getPasswordReset()!=null){
            id = user.getPasswordReset().getId();
            this.deletePasswordResetOtp(user);
        }
        if (passwordResetRepository.existsById(id)){
            throw new RuntimeException("Algo ha fallado en el servidor.");
        }

        int otp = otpGenerator();

        PasswordReset passwordReset = PasswordReset.builder()
                .otp(otp)
                .expirationTime(new Date(System.currentTimeMillis() + OTP_EXPIRATION_TIME))
                .user(user)
                .usagesCount(0)
                .build();
        passwordResetRepository.save(passwordReset);

        MailBodyResponse mailBodyResponse = MailBodyResponse.builder()
                .to(user.getEmail())
                .subject("Reset Password Code - Movie App")
                .text("\nValidació de usuario:"+
                        "\n\tSu código de verificación es " + otp + ".")
                .build();
        emailService.sendMimeEmail(mailBodyResponse);
    }

    public boolean verifyOtp(OtpVerifyRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Los datos ingresados no son válidos."));

        if (user.getRefreshToken()!=null){
            refreshTokenService.deleteRefreshToken(user.getRefreshToken());
        }

        Optional<PasswordReset> prOptional = passwordResetRepository.findByOtpAndUser(request.otp(), user);

        if (prOptional.isEmpty()){
            PasswordReset prFail = user.getPasswordReset();

            if (prFail==null) {
                throw new BadCredentialsException("Código de recuperación de cuenta inexistente. Por favor generar uno nuevo.");
            } else if (prFail.getUsagesCount() >= 3){
                this.deletePasswordResetOtp(user);
                throw new BadCredentialsException("El código ha expirado. Vuelva a generar uno nuevo.");
            } else {
                prFail.setUsagesCount(prFail.getUsagesCount()+1);
                passwordResetRepository.save(prFail);
            }

            return false;
        }

        PasswordReset pr = prOptional.get();

        if (pr.getExpirationTime().before(Date.from(Instant.now()))){
            this.deletePasswordResetOtp(user);
            throw new BadCredentialsException("El código ha expirado. Vuelva a generar uno nuevo.");
        }
        this.deletePasswordResetOtp(user);
        return true;
    }

    public void changePassword(ChangePasswordRequest request){
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("Dirección de email inválida."));

        if (jwtService.isTokenExpirated(request.PasswordResetToken())
                && !jwtService.isTokenValid(request.PasswordResetToken(), user)){
            throw new BadCredentialsException("Token inválido o expirado.");
        }

        String email = jwtService.extractUsername(request.PasswordResetToken());
        if (!email.equals(request.email())){
            throw new BadCredentialsException("Los datos enviados son inválidos.");
        }

        user.setPassword(encoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    /*
     *
     */
    private Integer otpGenerator(){
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }

    private void deletePasswordResetOtp(User user){
        PasswordReset passwordReset = user.getPasswordReset();
        user.setPasswordReset(null);
        passwordResetRepository.deleteById(passwordReset.getId());
    }
}
