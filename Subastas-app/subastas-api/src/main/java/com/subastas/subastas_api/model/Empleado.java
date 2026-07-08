package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    @Column(name = "identificador")
    private Long identificador;

    @Column(name = "cargo")
    private String cargo;

    @Column(name = "sector")
    private Integer sector;

    public Empleado() {
    }

    public Long getIdentificador() {
        return identificador;
    }

    public String getCargo() {
        return cargo;
    }

    public Integer getSector() {
        return sector;
    }
}