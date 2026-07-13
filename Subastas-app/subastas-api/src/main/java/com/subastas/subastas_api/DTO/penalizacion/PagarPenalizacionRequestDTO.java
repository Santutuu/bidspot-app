package com.subastas.subastas_api.DTO.penalizacion;

public class PagarPenalizacionRequestDTO {

    private Long idMedioPago;

    public PagarPenalizacionRequestDTO() {
    }

    public Long getIdMedioPago() {
        return idMedioPago;
    }

    public void setIdMedioPago(
            Long idMedioPago
    ) {
        this.idMedioPago =
                idMedioPago;
    }
}