package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCatalogo;

    private String descripcion;

    @OneToOne
    @JoinColumn(name = "subasta_id", nullable = false, unique = true)
    private Subasta subasta;

    @OneToMany(mappedBy = "catalogo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCatalogo> items = new ArrayList<>();

    public Catalogo() {
    }

    public Catalogo(String descripcion) {
        this.descripcion = descripcion;
    }

    public void agregarItem(ItemCatalogo itemCatalogo) {
        items.add(itemCatalogo);
        itemCatalogo.setCatalogo(this);
    }

    public void eliminarItem(ItemCatalogo itemCatalogo) {
        items.remove(itemCatalogo);
        itemCatalogo.setCatalogo(null);
    }

    public Long getIdCatalogo() {
        return idCatalogo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Subasta getSubasta() {
        return subasta;
    }

    public List<ItemCatalogo> getItems() {
        return items;
    }

    public void setSubasta(Subasta subasta) {
        this.subasta = subasta;
    }
}