package com.subastas.subastas_api.DTO.mediosPago;

public class TarjetaResponseDTO {

    private Long idTarjeta;
    private String numeroEnmascarado;
    private String nombre;
    private String fechaVto;
    private String moneda;
    private Float limiteCredito;

    public TarjetaResponseDTO(Long idTarjeta,
                              String numeroEnmascarado,
                              String nombre,
                              String fechaVto,
                              String moneda,
                              Float limiteCredito) {
        this.idTarjeta = idTarjeta;
        this.numeroEnmascarado = numeroEnmascarado;
        this.nombre = nombre;
        this.fechaVto = fechaVto;
        this.moneda = moneda;
        this.limiteCredito = limiteCredito;
    }

    public Long getIdTarjeta() {
        return idTarjeta;
    }

    public String getNumeroEnmascarado() {
        return numeroEnmascarado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFechaVto() {
        return fechaVto;
    }

    public String getMoneda() {
        return moneda;
    }

    public Float getLimiteCredito() {
        return limiteCredito;
    }
}