package com.subastas.subastas_api.DTO.mediosPago;

public class TarjetaResponseDTO {

    private Long idTarjeta;
    private String numeroEnmascarado;
    private String nombre;
    private String fechaVto;

    public TarjetaResponseDTO(Long idTarjeta,
                              String numeroEnmascarado,
                              String nombre,
                              String fechaVto) {
        this.idTarjeta = idTarjeta;
        this.numeroEnmascarado = numeroEnmascarado;
        this.nombre = nombre;
        this.fechaVto = fechaVto;
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
}