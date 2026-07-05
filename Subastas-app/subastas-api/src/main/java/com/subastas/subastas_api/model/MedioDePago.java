package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class MedioDePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedioPago;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Moneda moneda;

    public MedioDePago() {
    }

    public MedioDePago(Usuario usuario, Moneda moneda) {
        this.usuario = usuario;
        this.moneda = moneda;
    }

    public Long getIdMedioPago() {
        return idMedioPago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }
}