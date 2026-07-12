package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "registrodesubasta")
public class RegistroSubasta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long identificador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subasta", nullable = false)
    private Subasta subasta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "duenio", nullable = false)
    private Duenio duenio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto", nullable = false)
    private Item producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "importe", nullable = false)
    private BigDecimal importe;

    @Column(name = "comision", nullable = false)
    private BigDecimal comision;

    public RegistroSubasta() {
    }

    public RegistroSubasta(
            Subasta subasta,
            Duenio duenio,
            Item producto,
            Cliente cliente,
            BigDecimal importe,
            BigDecimal comision
    ) {
        this.subasta = subasta;
        this.duenio = duenio;
        this.producto = producto;
        this.cliente = cliente;
        this.importe = importe;
        this.comision = comision;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public Subasta getSubasta() {
        return subasta;
    }

    public Duenio getDuenio() {
        return duenio;
    }

    public Item getProducto() {
        return producto;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public BigDecimal getComision() {
        return comision;
    }
}