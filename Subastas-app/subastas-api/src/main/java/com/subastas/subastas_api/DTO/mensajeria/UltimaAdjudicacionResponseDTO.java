package com.subastas.subastas_api.DTO.mensajeria;

import java.time.LocalDateTime;

public class UltimaAdjudicacionResponseDTO {

    private boolean tieneAdjudicacion;

    private Long idVenta;

    private Long idItemCatalogo;

    private String tituloItem;

    private String imagenUrl;

    private Float montoPuja;

    private Float comision;

    private Float costoEnvio;

    private Float total;

    private String moneda;

    private String estado;

    private LocalDateTime fechaVenta;

    private LocalDateTime fechaLimitePago;

    public UltimaAdjudicacionResponseDTO(
            boolean tieneAdjudicacion,
            Long idVenta,
            Long idItemCatalogo,
            String tituloItem,
            String imagenUrl,
            Float montoPuja,
            Float comision,
            Float costoEnvio,
            Float total,
            String moneda,
            String estado,
            LocalDateTime fechaVenta,
            LocalDateTime fechaLimitePago
    ) {
        this.tieneAdjudicacion =
                tieneAdjudicacion;
        this.idVenta =
                idVenta;
        this.idItemCatalogo =
                idItemCatalogo;
        this.tituloItem =
                tituloItem;
        this.imagenUrl =
                imagenUrl;
        this.montoPuja =
                montoPuja;
        this.comision =
                comision;
        this.costoEnvio =
                costoEnvio;
        this.total =
                total;
        this.moneda =
                moneda;
        this.estado =
                estado;
        this.fechaVenta =
                fechaVenta;
        this.fechaLimitePago =
                fechaLimitePago;
    }

    public boolean isTieneAdjudicacion() {
        return tieneAdjudicacion;
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public Long getIdItemCatalogo() {
        return idItemCatalogo;
    }

    public String getTituloItem() {
        return tituloItem;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public Float getMontoPuja() {
        return montoPuja;
    }

    public Float getComision() {
        return comision;
    }

    public Float getCostoEnvio() {
        return costoEnvio;
    }

    public Float getTotal() {
        return total;
    }

    public String getMoneda() {
        return moneda;
    }

    public String getEstado() {
        return estado;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public LocalDateTime getFechaLimitePago() {
        return fechaLimitePago;
    }
}