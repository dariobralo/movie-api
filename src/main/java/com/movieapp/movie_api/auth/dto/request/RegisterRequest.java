package com.movieapp.movie_api.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record RegisterRequest (
        @NotBlank(message = "Introdusca una dirección de email válida.")
        @Email(message = "Introdusca una dirección de email válida.")
        String email,
        @NotBlank(message = "Introdusca un nombre de usuario. " +
                "Debe tener un minimo 3 caracteres y máximo de 20.")
        @Size(min = 3, max = 20, message = "Introdusca un nombre de usuario. " +
                "Debe tener un minimo 3 caracteres y máximo de 20.")
        String username,
        @NotBlank(message = "Introdusca una contraseña con un mínimo de 8 caracteres.")
        @Size(min = 8, message = "Introdusca una contraseña con un mínimo de 8 caracteres.")
        String password
){}
