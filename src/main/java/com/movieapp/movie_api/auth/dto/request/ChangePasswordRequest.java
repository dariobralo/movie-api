package com.movieapp.movie_api.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ChangePasswordRequest (
        @NotBlank(message = "Envie una dirección de email válida.")
        @Email(message = "Envie una dirección de email válida.")
        String email,
        @NotBlank(message = "Ingrese el token de usuario.")
        String PasswordResetToken,
        @NotBlank(message = "Ingrese una contraseña con un minimo de 8 dígitos.")
        @Size(min = 8, message = "Ingrese una contraseña con un minimo de 8 dígitos.")
        String newPassword,
        @NotBlank(message = "Ingrese una contraseña con un minimo de 8 dígitos.")
        @Size(min = 8, message = "Ingrese una contraseña con un minimo de 8 dígitos.")
        String repeatNewPassword
){
}
