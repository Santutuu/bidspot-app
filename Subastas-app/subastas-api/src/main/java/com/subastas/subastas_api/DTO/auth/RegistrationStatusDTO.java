package com.subastas.subastas_api.DTO.auth;

public class RegistrationStatusDTO {

    private String mail;
    private String estado;
    private String categoria;
    private boolean puedeGenerarClave;
    private String mensaje;

    public RegistrationStatusDTO(String mail,
                                 String estado,
                                 String categoria,
                                 boolean puedeGenerarClave,
                                 String mensaje) {
        this.mail = mail;
        this.estado = estado;
        this.categoria = categoria;
        this.puedeGenerarClave = puedeGenerarClave;
        this.mensaje = mensaje;
    }

    public String getMail() {
        return mail;
    }

    public String getEstado() {
        return estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public boolean isPuedeGenerarClave() {
        return puedeGenerarClave;
    }

    public String getMensaje() {
        return mensaje;
    }
}
