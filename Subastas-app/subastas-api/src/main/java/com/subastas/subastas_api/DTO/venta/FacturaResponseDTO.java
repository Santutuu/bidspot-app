package com.subastas.subastas_api.DTO.venta;

import java.util.Date;

public class FacturaResponseDTO {

    private Long idFactura;

    private Date fechaEmision;

    private Long idVenta;

    private Long idItemCatalogo;

    private String tituloItem;

    private Float montoPuja;

    private Float comision;

    private Float costoEnvio;

    private Float total;

    private String moneda;

    public FacturaResponseDTO(
            Long idFactura,
            Date fechaEmision,
            Long idVenta,
            Long idItemCatalogo,
            String tituloItem,
            Float montoPuja,
            Float comision,
            Float costoEnvio,
            Float total,
            String moneda
    ) {
        this.idFactura = idFactura;
        this.fechaEmision = fechaEmision;
        this.idVenta = idVenta;
        this.idItemCatalogo =
                idItemCatalogo;
        this.tituloItem =
                tituloItem;
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
    }

    public Long getIdFactura() {
        return idFactura;
    }

    public Date getFechaEmision() {
        return fechaEmision;
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
}