package com.subastas.subastas_api.DTO.publicacion;

import com.subastas.subastas_api.model.Moneda;

public class RechazarSolicitudRequestDTO {

    private String motivo;

    private Float costoDevolucion;

    private Moneda moneda;

    public RechazarSolicitudRequestDTO() {
    }

    public String getMotivo() {
        return motivo;
    }

    public Float getCostoDevolucion() {
        return costoDevolucion;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMotivo(
            String motivo
    ) {
        this.motivo = motivo;
    }

    public void setCostoDevolucion(
            Float costoDevolucion
    ) {
        this.costoDevolucion =
                costoDevolucion;
    }

    public void setMoneda(
            Moneda moneda
    ) {
        this.moneda = moneda;
    }
}