package com.subastas.subastas_api.DTO.puja;

import java.time.LocalDateTime;

public class PujaResponseDTO {

    private Integer idPuja;
    private Long idSubasta;
    private Long idItemCatalogo;
    private Float monto;
    private String moneda;
    private String estado;
    private LocalDateTime fechaHora;
    private boolean mejorOferta;

    public PujaResponseDTO(Integer idPuja,
                           Long idSubasta,
                           Long idItemCatalogo,
                           Float monto,
                           String moneda,
                           String estado,
                           LocalDateTime fechaHora,
                           boolean mejorOferta) {
        this.idPuja = idPuja;
        this.idSubasta = idSubasta;
        this.idItemCatalogo = idItemCatalogo;
        this.monto = monto;
        this.moneda = moneda;
        this.estado = estado;
        this.fechaHora = fechaHora;
        this.mejorOferta = mejorOferta;
    }

    public Integer getIdPuja() {
        return idPuja;
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

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public boolean isMejorOferta() {
        return mejorOferta;
    }
}