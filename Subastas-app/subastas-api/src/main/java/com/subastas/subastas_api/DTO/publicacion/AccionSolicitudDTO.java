package com.subastas.subastas_api.DTO.publicacion;

import java.time.LocalDateTime;

public class AccionSolicitudDTO {

    private Long idAccion;

    private String tipo;

    private String estado;

    private String titulo;

    private String descripcion;

    private Boolean aceptada;

    private String comentarioRespuesta;

    private String archivoUrl;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaResolucion;

    public AccionSolicitudDTO() {
    }

    public AccionSolicitudDTO(
            Long idAccion,
            String tipo,
            String estado,
            String titulo,
            String descripcion,
            Boolean aceptada,
            String comentarioRespuesta,
            String archivoUrl,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaResolucion
    ) {
        this.idAccion = idAccion;
        this.tipo = tipo;
        this.estado = estado;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.aceptada = aceptada;
        this.comentarioRespuesta =
                comentarioRespuesta;
        this.archivoUrl = archivoUrl;
        this.fechaCreacion = fechaCreacion;
        this.fechaResolucion = fechaResolucion;
    }

    public Long getIdAccion() {
        return idAccion;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEstado() {
        return estado;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Boolean getAceptada() {
        return aceptada;
    }

    public String getComentarioRespuesta() {
        return comentarioRespuesta;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }
}