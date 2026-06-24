package com.subastas.subastas_api.DTO.mediosPago;

public class MedioPagoResponseDTO {

    private Long idMedioPago;
    private String tipo;
    private String descripcion;

    public MedioPagoResponseDTO(Long idMedioPago,
                                String tipo,
                                String descripcion) {
        this.idMedioPago = idMedioPago;
        this.tipo = tipo;
        this.descripcion = descripcion;
    }

    public Long getIdMedioPago() {
        return idMedioPago;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }
}