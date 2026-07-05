package com.subastas.subastas_api.DTO.mediosPago;

import com.subastas.subastas_api.model.Moneda;

public class TarjetaRequestDTO {

    private String numero;
    private String nombre;
    private String fechaVto;
    private String cvv;
    private Moneda moneda;

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

    public Moneda getMoneda() {
        return moneda;
    }
}