package com.subastas.subastas_api.DTO.mediosPago;

public class TarjetaRequestDTO {

    private String numero;
    private String nombre;
    private String fechaVto;
    private String cvv;

    public String getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFechaVto() {
        return fechaVto;
    }

    public String getCvv() {
        return cvv;
    }
}