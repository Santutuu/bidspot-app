package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estadositemcatalogo")
public class EstadoItemCatalogoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long identificador;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre", nullable = false, unique = true)
    private EstadoItemCatalogo nombre;

    public EstadoItemCatalogoEntity() {
    }

    public EstadoItemCatalogoEntity(EstadoItemCatalogo nombre) {
        this.nombre = nombre;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public EstadoItemCatalogo getNombre() {
        return nombre;
    }

    public void setNombre(EstadoItemCatalogo nombre) {
        this.nombre = nombre;
    }
}