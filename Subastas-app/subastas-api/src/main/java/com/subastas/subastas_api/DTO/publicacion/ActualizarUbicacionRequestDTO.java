package com.subastas.subastas_api.DTO.publicacion;

public class ActualizarUbicacionRequestDTO {

    private String ubicacionActual;

    public ActualizarUbicacionRequestDTO() {
    }

    public String getUbicacionActual() {
        return ubicacionActual;
    }

    public void setUbicacionActual(
            String ubicacionActual
    ) {
        this.ubicacionActual =
                ubicacionActual;
    }
}