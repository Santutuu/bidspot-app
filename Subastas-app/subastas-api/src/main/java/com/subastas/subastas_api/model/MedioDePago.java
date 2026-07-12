package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class MedioDePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedioPago;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Moneda moneda;

    public MedioDePago() {
    }

    public MedioDePago(Cliente cliente,
                       Moneda moneda) {
        this.cliente = cliente;
        this.moneda = moneda;
    }

    public Long getIdMedioPago() {
        return idMedioPago;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }
}