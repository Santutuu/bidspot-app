package com.subastas.subastas_api.DTO.penalizacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PenalizacionResponseDTO {

    private Long idPenalizacion;

    private Long idVenta;

    private BigDecimal importe;

    private String moneda;

    private String tipo;

    private String estado;

    private LocalDateTime fechaGeneracion;

    private LocalDateTime fechaPago;

    public PenalizacionResponseDTO() {
    }

    public PenalizacionResponseDTO(
            Long idPenalizacion,
            Long idVenta,
            BigDecimal importe,
            String moneda,
            String tipo,
            String estado,
            LocalDateTime fechaGeneracion,
            LocalDateTime fechaPago
    ) {
        this.idPenalizacion =
                idPenalizacion;

        this.idVenta =
                idVenta;

        this.importe =
                importe;

        this.moneda =
                moneda;

        this.tipo =
                tipo;

        this.estado =
                estado;

        this.fechaGeneracion =
                fechaGeneracion;

        this.fechaPago =
                fechaPago;
    }

    public Long getIdPenalizacion() {
        return idPenalizacion;
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public String getMoneda() {
        return moneda;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
}