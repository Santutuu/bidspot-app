package com.subastas.subastas_api.DTO.venta;

public class ConfirmarCompraRequestDTO {

    private Long idMedioPago;
    private String tipoEntrega;
    private String direccionEntrega;

    public Long getIdMedioPago() {
        return idMedioPago;
    }

    public String getTipoEntrega() {
        return tipoEntrega;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }
}