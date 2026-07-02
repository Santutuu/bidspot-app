package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPoliza;

    private String nroPoliza;

    private Float montoAsegurado;

    private Float premio;

    private Float precioBase;

    private Float tasaSeguro;

    private String compania;

    @Enumerated(EnumType.STRING)
    private EstadoPoliza estado = EstadoPoliza.PROPUESTA;

    public Poliza() {
    }

    public Poliza(String nroPoliza,
                  Float montoAsegurado,
                  Float premio,
                  Float precioBase,
                  Float tasaSeguro,
                  String compania) {
        this.nroPoliza = nroPoliza;
        this.montoAsegurado = montoAsegurado;
        this.premio = premio;
        this.precioBase = precioBase;
        this.tasaSeguro = tasaSeguro;
        this.compania = compania;
        this.estado = EstadoPoliza.PROPUESTA;
    }

    public void aceptar() {
        this.estado = EstadoPoliza.ACEPTADA;
    }

    public void rechazar() {
        this.estado = EstadoPoliza.RECHAZADA;
    }

    public void solicitarAumento(Float nuevoMontoAsegurado) {
        this.montoAsegurado = nuevoMontoAsegurado;
        this.premio = nuevoMontoAsegurado * tasaSeguro;
        this.estado = EstadoPoliza.AUMENTO_SOLICITADO;
    }

    public Long getIdPoliza() {
        return idPoliza;
    }

    public String getNroPoliza() {
        return nroPoliza;
    }

    public Float getMontoAsegurado() {
        return montoAsegurado;
    }

    public Float getPremio() {
        return premio;
    }

    public Float getPrecioBase() {
        return precioBase;
    }

    public Float getTasaSeguro() {
        return tasaSeguro;
    }

    public String getCompania() {
        return compania;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }
}