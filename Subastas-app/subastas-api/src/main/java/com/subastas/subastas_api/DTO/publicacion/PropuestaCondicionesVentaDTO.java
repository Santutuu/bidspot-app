package com.subastas.subastas_api.DTO.publicacion;

import java.time.LocalDateTime;

public class PropuestaCondicionesVentaDTO {

    private Long idPropuesta;

    private String estado;

    private Float precioBase;

    private Float porcentajeComision;

    private Long idSubasta;

    private String tituloSubasta;

    private String categoriaMinima;

    private String moneda;

    private LocalDateTime fechaSubasta;

    private String ubicacionSubasta;

    private String rematador;

    private String motivoRechazoUsuario;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaRespuesta;

    public PropuestaCondicionesVentaDTO() {
    }

    public PropuestaCondicionesVentaDTO(
            Long idPropuesta,
            String estado,
            Float precioBase,
            Float porcentajeComision,
            Long idSubasta,
            String tituloSubasta,
            String categoriaMinima,
            String moneda,
            LocalDateTime fechaSubasta,
            String ubicacionSubasta,
            String rematador,
            String motivoRechazoUsuario,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaRespuesta
    ) {
        this.idPropuesta = idPropuesta;
        this.estado = estado;
        this.precioBase = precioBase;
        this.porcentajeComision =
                porcentajeComision;
        this.idSubasta = idSubasta;
        this.tituloSubasta = tituloSubasta;
        this.categoriaMinima = categoriaMinima;
        this.moneda = moneda;
        this.fechaSubasta = fechaSubasta;
        this.ubicacionSubasta =
                ubicacionSubasta;
        this.rematador = rematador;
        this.motivoRechazoUsuario =
                motivoRechazoUsuario;
        this.fechaCreacion = fechaCreacion;
        this.fechaRespuesta = fechaRespuesta;
    }

    public Long getIdPropuesta() {
        return idPropuesta;
    }

    public String getEstado() {
        return estado;
    }

    public Float getPrecioBase() {
        return precioBase;
    }

    public Float getPorcentajeComision() {
        return porcentajeComision;
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public String getTituloSubasta() {
        return tituloSubasta;
    }

    public String getCategoriaMinima() {
        return categoriaMinima;
    }

    public String getMoneda() {
        return moneda;
    }

    public LocalDateTime getFechaSubasta() {
        return fechaSubasta;
    }

    public String getUbicacionSubasta() {
        return ubicacionSubasta;
    }

    public String getRematador() {
        return rematador;
    }

    public String getMotivoRechazoUsuario() {
        return motivoRechazoUsuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }
}