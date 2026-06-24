package com.subastas.subastas_api.DTO.mediosPago;

import java.util.Date;

public class TarjetaResponseDTO {

    private Long idMedioPago;
    private String tipo;
    private String numero;
    private String nombre;
    private String fechaVto;


    public TarjetaResponseDTO(Long idMedioPago,
                              String numero,
                              String nombre,
                              String fechaVto
                             ) {
        this.idMedioPago = idMedioPago;
        this.tipo = "TARJETA";
        this.numero = numero;
        this.nombre = nombre;
        this.fechaVto = fechaVto;

    }

    public Long getIdMedioPago() {
        return idMedioPago;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFechaVto() {
        return fechaVto;
    }

}