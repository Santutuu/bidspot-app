package com.subastas.subastas_api.DTO.mediosPago;

public class MedioPagoResponseDTO {

    private Long idMedioPago;
    private String tipo;
    private String descripcion;
    private String moneda;
    private Float capacidad;

    public MedioPagoResponseDTO(Long idMedioPago,
                                String tipo,
                                String descripcion,
                                String moneda,
                                Float capacidad) {
        this.idMedioPago = idMedioPago;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.moneda = moneda;
        this.capacidad = capacidad;
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

    public String getMoneda() {
        return moneda;
    }

    public Float getCapacidad() {
        return capacidad;
    }
}