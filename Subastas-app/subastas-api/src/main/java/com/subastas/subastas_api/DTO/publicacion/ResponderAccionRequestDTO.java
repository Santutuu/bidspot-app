package com.subastas.subastas_api.DTO.publicacion;

import com.subastas.subastas_api.model.TipoRespuestaAccion;

public class ResponderAccionRequestDTO {

    private TipoRespuestaAccion tipoRespuesta;
    private Boolean aceptada;
    private String comentario;
    private String archivoUrl;
    private Float montoAseguradoSolicitado;

    public TipoRespuestaAccion getTipoRespuesta() {
        return tipoRespuesta;
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

    public Float getMontoAseguradoSolicitado() {
        return montoAseguradoSolicitado;
    }
}