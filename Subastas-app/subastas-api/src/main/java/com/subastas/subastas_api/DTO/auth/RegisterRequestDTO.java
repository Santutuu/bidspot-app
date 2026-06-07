package com.subastas.subastas_api.DTO.auth;

import com.subastas.subastas_api.model.Domicilio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String apellido;

    @NotBlank(message = "El mail es obligatorio")
    @Email(message = "El formato del email no es válido")
    private String mail;

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
            message = "La contraseña debe tener mínimo 8 caracteres, una mayúscula, un número y un carácter especial"
    )
    private String password;

    @NotBlank(message = "La foto del frente del DNI es obligatoria")
    private String frenteDNIUrl;

    @NotBlank(message = "La foto del dorso del DNI es obligatoria")
    private String dorsoDNIUrl;

    @Valid
    @NotNull(message = "El domicilio es obligatorio")
    private Domicilio domicilio;

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getMail() {
        return mail;
    }

    public String getPassword() {
        return password;
    }

    public String getFrenteDNIUrl() {
        return frenteDNIUrl;
    }

    public String getDorsoDNIUrl() {
        return dorsoDNIUrl;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }
}