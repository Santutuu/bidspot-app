package com.subastas.subastas_api.DTO.auth;

public class UsuarioActualDTO {

    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String mail;
    private String rol;
    private String estado;

    public UsuarioActualDTO(Long idUsuario,
                            String nombre,
                            String apellido,
                            String mail,
                            String rol,
                            String estado) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.rol = rol;
        this.estado = estado;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
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