package com.subastas.subastas_api.DTO.publicacion;

public class ConfigurarDevolucionRequestDTO {

    private String direccionDestino;

    private Long idMedioPago;

    public ConfigurarDevolucionRequestDTO() {
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public Long getIdMedioPago() {
        return idMedioPago;
    }

    public void setDireccionDestino(
            String direccionDestino
    ) {
        this.direccionDestino =
                direccionDestino;
    }

    public void setIdMedioPago(
            Long idMedioPago
    ) {
        this.idMedioPago = idMedioPago;
    }
}