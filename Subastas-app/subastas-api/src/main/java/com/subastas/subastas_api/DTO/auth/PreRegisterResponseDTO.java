package com.subastas.subastas_api.DTO.auth;

public class PreRegisterResponseDTO {

    private Long idUsuario;
    private String nombre;
    private String mail;
    private String estado;
    private String mensaje;

    public PreRegisterResponseDTO(Long idUsuario,
                                  String nombre,
                                  String mail,
                                  String estado,
                                  String mensaje) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.mail = mail;
        this.estado = estado;
        this.mensaje = mensaje;
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

    public String getEstado() {
        return estado;
    }

    public String getMensaje() {
        return mensaje;
    }
}