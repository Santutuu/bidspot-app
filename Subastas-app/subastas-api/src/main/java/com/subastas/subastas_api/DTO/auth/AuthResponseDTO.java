package com.subastas.subastas_api.DTO.auth;

public class AuthResponseDTO {

    private String token;
    private Long idUsuario;
    private String nombre;
    private String mail;
    private String rol;
    private String estado;
    private String categoria;
    private boolean tieneMedioPago;
    private boolean configuracionFinancieraCompleta;

    public AuthResponseDTO(String token,
                           Long idUsuario,
                           String nombre,
                           String mail,
                           String rol,
                           String estado,
                           String categoria,
                           boolean tieneMedioPago,
                           boolean configuracionFinancieraCompleta) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.mail = mail;
        this.rol = rol;
        this.estado = estado;
        this.categoria = categoria;
        this.tieneMedioPago = tieneMedioPago;
        this.configuracionFinancieraCompleta = configuracionFinancieraCompleta;
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

    public String getCategoria() {
        return categoria;
    }

    public boolean isTieneMedioPago() {
        return tieneMedioPago;
    }

    public boolean isConfiguracionFinancieraCompleta() {
        return configuracionFinancieraCompleta;
    }
}
