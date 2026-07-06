package com.subastas.subastas_api.DTO.venta;

public class VentaResumenResponseDTO {

    private Long idVenta;
    private String tituloItem;
    private String imagenUrl;
    private Float total;
    private String moneda;
    private String estado;

    public VentaResumenResponseDTO(Long idVenta,
                                   String tituloItem,
                                   String imagenUrl,
                                   Float total,
                                   String moneda,
                                   String estado) {
        this.idVenta = idVenta;
        this.tituloItem = tituloItem;
        this.imagenUrl = imagenUrl;
        this.total = total;
        this.moneda = moneda;
        this.estado = estado;
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public String getTituloItem() {
        return tituloItem;
    }

    public String getImagenUrl() {
        return imagenUrl;
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
}