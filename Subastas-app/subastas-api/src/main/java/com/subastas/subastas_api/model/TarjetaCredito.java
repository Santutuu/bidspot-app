package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
public class TarjetaCredito extends MedioDePago {

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String fechaVto;

    @Column(nullable = false)
    private String cvv;

    private boolean principal;

    public TarjetaCredito() {
    }

    public TarjetaCredito(String numero,
                          String nombre,
                          String fechaVto,
                          String cvv,
                          boolean principal) {
        this.numero = numero;
        this.nombre = nombre;
        this.fechaVto = fechaVto;
        this.cvv = cvv;
        this.principal = principal;
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

    public String getCvv() {
        return cvv;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
}