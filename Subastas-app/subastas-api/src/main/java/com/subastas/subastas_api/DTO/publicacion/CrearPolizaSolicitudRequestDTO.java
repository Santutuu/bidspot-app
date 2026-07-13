package com.subastas.subastas_api.DTO.poliza;

public class CrearPolizaSolicitudRequestDTO {

    private String numeroPoliza;

    private String compania;

    private Float montoAsegurado;

    /*
     * Porcentaje.
     *
     * Ejemplo:
     * 5 representa 5 %.
     */
    private Float tasaSeguro;

    public CrearPolizaSolicitudRequestDTO() {
    }

    public String getNumeroPoliza() {
        return numeroPoliza;
    }

    public String getCompania() {
        return compania;
    }

    public Float getMontoAsegurado() {
        return montoAsegurado;
    }

    public Float getTasaSeguro() {
        return tasaSeguro;
    }

    public void setNumeroPoliza(
            String numeroPoliza
    ) {
        this.numeroPoliza = numeroPoliza;
    }

    public void setCompania(
            String compania
    ) {
        this.compania = compania;
    }

    public void setMontoAsegurado(
            Float montoAsegurado
    ) {
        this.montoAsegurado = montoAsegurado;
    }

    public void setTasaSeguro(
            Float tasaSeguro
    ) {
        this.tasaSeguro = tasaSeguro;
    }
}