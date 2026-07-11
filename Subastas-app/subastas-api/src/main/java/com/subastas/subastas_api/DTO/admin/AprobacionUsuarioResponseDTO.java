package com.subastas.subastas_api.DTO.admin;

public class AprobacionUsuarioResponseDTO {

    private Long idUsuario;
    private Long idPersona;
    private Long idCliente;
    private String mail;
    private String estado;
    private String admitido;
    private String categoria;

    public AprobacionUsuarioResponseDTO(Long idUsuario,
                                        Long idPersona,
                                        Long idCliente,
                                        String mail,
                                        String estado,
                                        String admitido,
                                        String categoria) {
        this.idUsuario = idUsuario;
        this.idPersona = idPersona;
        this.idCliente = idCliente;
        this.mail = mail;
        this.estado = estado;
        this.admitido = admitido;
        this.categoria = categoria;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Long getIdPersona() {
        return idPersona;
    }

    public Long getIdCliente() {
        return idCliente;
    }

    public String getMail() {
        return mail;
    }

    public String getEstado() {
        return estado;
    }

    public String getAdmitido() {
        return admitido;
    }

    public String getCategoria() {
        return categoria;
    }
}