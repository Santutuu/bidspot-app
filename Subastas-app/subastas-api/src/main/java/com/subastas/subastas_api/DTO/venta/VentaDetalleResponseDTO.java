package com.subastas.subastas_api.DTO.venta;

import java.time.LocalDateTime;

public class VentaDetalleResponseDTO {

    private Long idVenta;
    private Long idItemCatalogo;
    private Long idSubasta;

    private String tituloItem;
    private String imagenUrl;
    private String estado;

    private Float montoPuja;
    private Float comision;
    private Float costoEnvio;
    private Float total;
    private String moneda;

    private String tipoEntrega;
    private String direccionEntrega;
    private String ubicacionRetiro;

    private Long idMedioPago;

    private LocalDateTime fechaVenta;
    private LocalDateTime fechaPagoConfirmado;

    public VentaDetalleResponseDTO(Long idVenta,
                                   Long idItemCatalogo,
                                   Long idSubasta,
                                   String tituloItem,
                                   String imagenUrl,
                                   String estado,
                                   Float montoPuja,
                                   Float comision,
                                   Float costoEnvio,
                                   Float total,
                                   String moneda,
                                   String tipoEntrega,
                                   String direccionEntrega,
                                   String ubicacionRetiro,
                                   Long idMedioPago,
                                   LocalDateTime fechaVenta,
                                   LocalDateTime fechaPagoConfirmado) {
        this.idVenta = idVenta;
        this.idItemCatalogo = idItemCatalogo;
        this.idSubasta = idSubasta;
        this.tituloItem = tituloItem;
        this.imagenUrl = imagenUrl;
        this.estado = estado;
        this.montoPuja = montoPuja;
        this.comision = comision;
        this.costoEnvio = costoEnvio;
        this.total = total;
        this.moneda = moneda;
        this.tipoEntrega = tipoEntrega;
        this.direccionEntrega = direccionEntrega;
        this.ubicacionRetiro = ubicacionRetiro;
        this.idMedioPago = idMedioPago;
        this.fechaVenta = fechaVenta;
        this.fechaPagoConfirmado = fechaPagoConfirmado;
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public Long getIdItemCatalogo() {
        return idItemCatalogo;
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public String getTituloItem() {
        return tituloItem;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public String getEstado() {
        return estado;
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

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public String getUbicacionRetiro() {
        return ubicacionRetiro;
    }

    public Long getIdMedioPago() {
        return idMedioPago;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public LocalDateTime getFechaPagoConfirmado() {
        return fechaPagoConfirmado;
    }
}