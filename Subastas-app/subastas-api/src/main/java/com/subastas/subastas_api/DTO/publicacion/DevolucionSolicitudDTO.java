package com.subastas.subastas_api.DTO.publicacion;

import java.time.LocalDateTime;

public class DevolucionSolicitudDTO {

    private Long idDevolucion;

    private String estado;

    private Float costo;

    private String moneda;

    private String direccionDestino;

    private Long idMedioPago;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaPago;

    private LocalDateTime fechaEnvio;

    private LocalDateTime fechaEntrega;

    public DevolucionSolicitudDTO() {
    }

    public DevolucionSolicitudDTO(
            Long idDevolucion,
            String estado,
            Float costo,
            String moneda,
            String direccionDestino,
            Long idMedioPago,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaPago,
            LocalDateTime fechaEnvio,
            LocalDateTime fechaEntrega
    ) {
        this.idDevolucion = idDevolucion;
        this.estado = estado;
        this.costo = costo;
        this.moneda = moneda;
        this.direccionDestino =
                direccionDestino;
        this.idMedioPago = idMedioPago;
        this.fechaCreacion = fechaCreacion;
        this.fechaPago = fechaPago;
        this.fechaEnvio = fechaEnvio;
        this.fechaEntrega = fechaEntrega;
    }

    public Long getIdDevolucion() {
        return idDevolucion;
    }

    public String getEstado() {
        return estado;
    }

    public Float getCosto() {
        return costo;
    }

    public String getMoneda() {
        return moneda;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public Long getIdMedioPago() {
        return idMedioPago;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }
}