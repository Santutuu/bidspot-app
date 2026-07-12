package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "catalogos")
public class Catalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long idCatalogo;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subasta")
    private Subasta subasta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsable", nullable = false)
    private Empleado responsable;

    @OneToMany(
            mappedBy = "catalogo",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemCatalogo> items =
            new ArrayList<>();

    public Catalogo() {
    }

    public Catalogo(
            String descripcion,
            Empleado responsable
    ) {
        this.descripcion = descripcion;
        this.responsable = responsable;
    }

    public void agregarItem(
            ItemCatalogo itemCatalogo
    ) {
        if (itemCatalogo == null) {
            return;
        }

        items.add(itemCatalogo);
        itemCatalogo.setCatalogo(this);
    }

    public void eliminarItem(
            ItemCatalogo itemCatalogo
    ) {
        items.remove(itemCatalogo);

        if (itemCatalogo != null) {
            itemCatalogo.setCatalogo(null);
        }
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

    public Empleado getResponsable() {
        return responsable;
    }

    public List<ItemCatalogo> getItems() {
        return items;
    }

    public void setDescripcion(
            String descripcion
    ) {
        this.descripcion = descripcion;
    }

    public void setSubasta(
            Subasta subasta
    ) {
        this.subasta = subasta;
    }

    public void setResponsable(
            Empleado responsable
    ) {
        this.responsable = responsable;
    }
}