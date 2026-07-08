package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estadossubasta")
public class EstadoSubastaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long identificador;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre", nullable = false, unique = true)
    private EstadoSubasta nombre;

    public EstadoSubastaEntity() {
    }

    public EstadoSubastaEntity(EstadoSubasta nombre) {
        this.nombre = nombre;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public EstadoSubasta getNombre() {
        return nombre;
    }

    public void setNombre(EstadoSubasta nombre) {
        this.nombre = nombre;
    }
}