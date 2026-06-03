package com.subastas.subastas_api.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class CompraPendiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCompraPendiente;

    @ManyToOne
    private Usuario comprador;

    @OneToOne
    private Item item;

    private float monto;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaLimitePago;

    @Enumerated(EnumType.STRING)
    private EstadoCompraPendiente estado;

    public CompraPendiente() {
    }

    public CompraPendiente(Usuario comprador,
                           Item item,
                           float monto,
                           Date fechaLimitePago,
                           EstadoCompraPendiente estado) {

        this.comprador = comprador;
        this.item = item;
        this.monto = monto;
        this.fechaLimitePago = fechaLimitePago;
        this.estado = estado;
    }

    public Usuario getComprador() {
        return comprador;
    }

    public void setComprador(Usuario comprador) {
        this.comprador = comprador;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public Date getFechaLimitePago() {
        return fechaLimitePago;
    }

    public void setFechaLimitePago(Date fechaLimitePago) {
        this.fechaLimitePago = fechaLimitePago;
    }

    public EstadoCompraPendiente getEstado() {
        return estado;
    }

    public void setEstado(EstadoCompraPendiente estado) {
        this.estado = estado;
    }
}