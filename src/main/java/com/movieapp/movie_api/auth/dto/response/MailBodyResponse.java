package com.movieapp.movie_api.auth.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MailBodyResponse(
        @NotBlank(message = "Introducir una dirección de email válida")
        @Email(message = "Introducir una dirección de email válida")
        String to,
        @NotBlank(message = "Introducir asunto del mensaje.")
        String subject,
        @NotBlank(message = "Introducir cuerpo del mensaje.")
        String text
) {
}
