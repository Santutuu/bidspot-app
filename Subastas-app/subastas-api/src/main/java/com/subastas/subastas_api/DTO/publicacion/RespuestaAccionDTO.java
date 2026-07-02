package com.subastas.subastas_api.DTO.publicacion;

import java.time.LocalDateTime;

public class RespuestaAccionDTO {

    private Long idRespuesta;
    private String accion;
    private String tipoRespuesta;
    private Boolean aceptada;
    private String comentario;
    private String archivoUrl;
    private Float montoAseguradoSolicitado;
    private LocalDateTime fechaRespuesta;

    public RespuestaAccionDTO(Long idRespuesta,
                              String accion,
                              String tipoRespuesta,
                              Boolean aceptada,
                              String comentario,
                              String archivoUrl,
                              Float montoAseguradoSolicitado,
                              LocalDateTime fechaRespuesta) {
        this.idRespuesta = idRespuesta;
        this.accion = accion;
        this.tipoRespuesta = tipoRespuesta;
        this.aceptada = aceptada;
        this.comentario = comentario;
        this.archivoUrl = archivoUrl;
        this.montoAseguradoSolicitado = montoAseguradoSolicitado;
        this.fechaRespuesta = fechaRespuesta;
    }

    public Long getIdRespuesta() {
        return idRespuesta;
    }

    public String getAccion() {
        return accion;
    }

    public String getTipoRespuesta() {
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

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }
}