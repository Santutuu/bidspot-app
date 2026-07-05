package com.subastas.subastas_api.DTO.puja;

import java.time.LocalDateTime;

public class PujaActualizadaEventDTO {

    private Long idSubasta;
    private Long idItemCatalogo;
    private Float monto;
    private String moneda;
    private LocalDateTime fechaHora;

    public PujaActualizadaEventDTO(Long idSubasta,
                                   Long idItemCatalogo,
                                   Float monto,
                                   String moneda,
                                   LocalDateTime fechaHora) {
        this.idSubasta = idSubasta;
        this.idItemCatalogo = idItemCatalogo;
        this.monto = monto;
        this.moneda = moneda;
        this.fechaHora = fechaHora;
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public Long getIdItemCatalogo() {
        return idItemCatalogo;
    }

    public Float getMonto() {
        return monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
}