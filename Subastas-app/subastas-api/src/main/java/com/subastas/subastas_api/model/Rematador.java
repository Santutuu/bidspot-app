package com.subastas.subastas_api.model;
import jakarta.persistence.*;


@Entity
public class Rematador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRematador;

    private String matricula;
    private String nombre;
    private String apellido;

    public Rematador() {}

    public Rematador(String matricula, String nombre, String apellido) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public Long getIdRematador() { return idRematador; }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
}