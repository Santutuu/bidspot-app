package com.subastas.subastas_api.DTO.publicacion;

public class ConfirmarRecepcionRequestDTO {

    private String ubicacionActual;

    public ConfirmarRecepcionRequestDTO() {
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