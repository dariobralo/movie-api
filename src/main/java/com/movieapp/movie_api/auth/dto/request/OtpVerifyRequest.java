package com.movieapp.movie_api.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OtpVerifyRequest (
        @NotBlank(message = "Envie una dirección de email válida.")
        @Email(message = "Envie una dirección de email válida.")
        String email,
        @NotNull(message = "Ingrese el código de 6 dígitos")
        Integer otp
){}
