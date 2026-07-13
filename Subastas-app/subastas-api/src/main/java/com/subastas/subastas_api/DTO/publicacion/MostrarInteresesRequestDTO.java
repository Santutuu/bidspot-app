package com.subastas.subastas_api.DTO.publicacion;

import java.time.LocalDate;

public class MostrarInteresesRequestDTO {

    private String direccionDeposito;

    private LocalDate fechaLimiteEnvio;

    public void MostrarInteresRequestDTO() {
    }

    public String getDireccionDeposito() {
        return direccionDeposito;
    }

    public LocalDate getFechaLimiteEnvio() {
        return fechaLimiteEnvio;
    }

    public void setDireccionDeposito(
            String direccionDeposito
    ) {
        this.direccionDeposito =
                direccionDeposito;
    }

    public void setFechaLimiteEnvio(
            LocalDate fechaLimiteEnvio
    ) {
        this.fechaLimiteEnvio =
                fechaLimiteEnvio;
    }
}