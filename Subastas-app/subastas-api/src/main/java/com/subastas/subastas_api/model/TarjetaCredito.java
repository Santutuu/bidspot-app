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

    @Column(nullable = false)
    private Float limiteCredito;

    public TarjetaCredito() {
    }

    public TarjetaCredito(Cliente cliente,
                          String numero,
                          String nombre,
                          String fechaVto,
                          String cvv,
                          Moneda moneda,
                          Float limiteCredito) {
        super(cliente, moneda);
        this.numero = numero;
        this.nombre = nombre;
        this.fechaVto = fechaVto;
        this.cvv = cvv;
        this.limiteCredito = limiteCredito;
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

    public Float getLimiteCredito() {
        return limiteCredito;
    }

    public boolean tieneLimiteSuficiente(Float monto) {
        return monto != null
                && monto > 0f
                && limiteCredito != null
                && limiteCredito >= monto;
    }

    public void consumirLimite(Float monto) {

        if (!tieneLimiteSuficiente(monto)) {
            throw new IllegalStateException(
                    "La tarjeta no tiene límite suficiente"
            );
        }

        this.limiteCredito -= monto;
    }
}