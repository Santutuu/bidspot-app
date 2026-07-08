package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "asistentes")
public class Asistente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Integer identificador;

    @Column(name = "numeropostor", nullable = false)
    private Integer numeroPostor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subasta", nullable = false)
    private Subasta subasta;

    public Asistente() {
    }

    public Asistente(Integer numeroPostor, Cliente cliente, Subasta subasta) {
        this.numeroPostor = numeroPostor;
        this.cliente = cliente;
        this.subasta = subasta;
    }

    public Integer getIdentificador() {
        return identificador;
    }

    public Integer getNumeroPostor() {
        return numeroPostor;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Subasta getSubasta() {
        return subasta;
    }
}