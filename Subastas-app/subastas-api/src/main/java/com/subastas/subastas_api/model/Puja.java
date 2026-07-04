package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Puja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPuja;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subasta_id")
    private Subasta subasta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "item_catalogo_id")
    private ItemCatalogo itemCatalogo;

    @Column(nullable = false)
    private Float monto;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPuja estado = EstadoPuja.REGISTRADA;

    public Puja() {
    }

    public Puja(Usuario usuario,
                Subasta subasta,
                ItemCatalogo itemCatalogo,
                Float monto) {
        this.usuario = usuario;
        this.subasta = subasta;
        this.itemCatalogo = itemCatalogo;
        this.monto = monto;
        this.fechaHora = LocalDateTime.now();
        this.estado = EstadoPuja.REGISTRADA;
    }

    public Long getIdPuja() {
        return idPuja;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Subasta getSubasta() {
        return subasta;
    }

    public ItemCatalogo getItemCatalogo() {
        return itemCatalogo;
    }

    public Float getMonto() {
        return monto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setItemCatalogo(ItemCatalogo itemCatalogo) {
        this.itemCatalogo = itemCatalogo;
    }

    public EstadoPuja getEstado() {
        return estado;
    }

    public void marcarSuperada() {
        this.estado = EstadoPuja.SUPERADA;
    }

    public void marcarGanadora() {
        this.estado = EstadoPuja.GANADORA;
    }

    public void marcarRechazada() {
        this.estado = EstadoPuja.RECHAZADA;
    }
}