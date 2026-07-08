package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fotos")
public class Foto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long identificador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto", nullable = false)
    private Item producto;

    @Column(name = "foto", nullable = false, columnDefinition = "TEXT")
    private String foto;

    public Foto() {
    }

    public Foto(String foto) {
        this.foto = foto;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public Item getProducto() {
        return producto;
    }

    public String getFoto() {
        return foto;
    }

    public void setProducto(Item producto) {
        this.producto = producto;
    }
}