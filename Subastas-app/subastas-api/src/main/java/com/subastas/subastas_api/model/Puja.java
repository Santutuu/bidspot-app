package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Puja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPuja;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "item_catalogo_id", nullable = false)
    private ItemCatalogo itemCatalogo;

    @Column(nullable = false)
    private float monto;

    @Column(nullable = false)
    private LocalDateTime fechaHora;

    public Puja() {
    }

    public Puja(Usuario usuario, float monto) {
        this.usuario = usuario;
        this.monto = monto;
        this.fechaHora = LocalDateTime.now();
    }

    public Long getIdPuja() {
        return idPuja;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public ItemCatalogo getItemCatalogo() {
        return itemCatalogo;
    }

    public float getMonto() {
        return monto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setItemCatalogo(ItemCatalogo itemCatalogo) {
        this.itemCatalogo = itemCatalogo;
    }
}