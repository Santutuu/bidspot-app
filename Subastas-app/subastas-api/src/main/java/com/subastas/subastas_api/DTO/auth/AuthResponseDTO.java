package com.subastas.subastas_api.DTO.auth;

public class AuthResponseDTO {

    private String token;
    private Long idUsuario;
    private String nombre;
    private String mail;
    private String rol;
    private String estado;

    public AuthResponseDTO(String token,
                           Long idUsuario,
                           String nombre,
                           String mail,
                           String rol,
                           String estado) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.mail = mail;
        this.rol = rol;
        this.estado = estado;
    }

    public String getToken() {
        return token;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMail() {
        return mail;
    }

    public String getRol() {
        return rol;
    }

    public String getEstado() {
        return estado;
    }
}