package com.subastas.subastas_api.DTO.publicacion;

import java.time.LocalDateTime;

public class SolicitudPublicacionResumenDTO {

    private Long idSolicitud;

    private String titulo;

    private String estado;

    private String categoria;

    private String imagenUrl;

    private LocalDateTime fechaCreacion;

    private Integer cantidadAccionesPendientes;

    private Long idSubasta;

    private LocalDateTime fechaSubasta;

    public SolicitudPublicacionResumenDTO() {
    }

    public SolicitudPublicacionResumenDTO(
            Long idSolicitud,
            String titulo,
            String estado,
            String categoria,
            String imagenUrl,
            LocalDateTime fechaCreacion,
            Integer cantidadAccionesPendientes,
            Long idSubasta,
            LocalDateTime fechaSubasta
    ) {
        this.idSolicitud = idSolicitud;
        this.titulo = titulo;
        this.estado = estado;
        this.categoria = categoria;
        this.imagenUrl = imagenUrl;
        this.fechaCreacion = fechaCreacion;
        this.cantidadAccionesPendientes =
                cantidadAccionesPendientes;
        this.idSubasta = idSubasta;
        this.fechaSubasta = fechaSubasta;
    }

    public Long getIdSolicitud() {
        return idSolicitud;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getEstado() {
        return estado;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Integer getCantidadAccionesPendientes() {
        return cantidadAccionesPendientes;
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public LocalDateTime getFechaSubasta() {
        return fechaSubasta;
    }
}