package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subastadores")
public class Rematador {

    /*
     * Clave primaria compartida con personas.identificador.
     */
    @Id
    @Column(name = "identificador")
    private Long idRematador;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "identificador")
    private Persona persona;

    @Column(name = "matricula")
    private String matricula;

    @Column(name = "region")
    private String region;

    public Rematador() {
    }

    public Rematador(
            Persona persona,
            String matricula,
            String region
    ) {
        this.persona = persona;
        this.matricula = matricula;
        this.region = region;
    }

    public Long getIdRematador() {
        return idRematador;
    }

    public Persona getPersona() {
        return persona;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getRegion() {
        return region;
    }

    public String getNombre() {
        return persona != null
                ? persona.getNombre()
                : null;
    }

    public String getApellido() {
        return persona != null
                ? persona.getApellido()
                : null;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}