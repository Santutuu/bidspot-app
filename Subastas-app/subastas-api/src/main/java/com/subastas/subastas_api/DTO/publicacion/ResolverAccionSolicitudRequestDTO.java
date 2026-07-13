package com.subastas.subastas_api.DTO.publicacion;

public class ResolverAccionSolicitudRequestDTO {

    private Boolean aceptada;

    private String comentario;

    private String archivoUrl;

    public ResolverAccionSolicitudRequestDTO() {
    }

    public Boolean getAceptada() {
        return aceptada;
    }

    public String getComentario() {
        return comentario;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public void setAceptada(
            Boolean aceptada
    ) {
        this.aceptada = aceptada;
    }

    public void setComentario(
            String comentario
    ) {
        this.comentario = comentario;
    }

    public void setArchivoUrl(
            String archivoUrl
    ) {
        this.archivoUrl = archivoUrl;
    }
}