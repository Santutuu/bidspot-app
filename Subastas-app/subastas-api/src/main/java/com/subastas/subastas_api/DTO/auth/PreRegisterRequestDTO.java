package com.subastas.subastas_api.DTO.auth;

import com.subastas.subastas_api.model.Domicilio;

public class PreRegisterRequestDTO {

    private String nombre;
    private String apellido;
    private String documento;
    private String mail;
    private String frenteDNIUrl;
    private String dorsoDNIUrl;
    private Domicilio domicilio;

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public String getMail() {
        return mail;
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