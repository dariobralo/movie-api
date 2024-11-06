package com.movieapp.movie_api.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailVerifyRequest(
        @NotBlank(message = "Envie una dirección de email válida.")
        @Email(message = "Envie una dirección de email válida.")
        String email
) {
}
