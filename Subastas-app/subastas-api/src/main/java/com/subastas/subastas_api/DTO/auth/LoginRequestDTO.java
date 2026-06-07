package com.subastas.subastas_api.DTO.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "El mail es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String mail;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }
}