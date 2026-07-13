package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "duenios")
public class Duenio {

    /*
     * PK compartida con Persona.
     *
     * personas.identificador
     * clientes.identificador
     * duenios.identificador
     */
    @Id
    @Column(name = "identificador")
    private Long identificador;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @MapsId
    @JoinColumn(
            name = "identificador",
            nullable = false
    )
    private Persona persona;

    @Column(name = "numeropais")
    private Integer numeroPais;

    @Column(name = "verificacionfinanciera")
    private String verificacionFinanciera;

    @Column(name = "verificacionjudicial")
    private String verificacionJudicial;

    @Column(name = "calificacionriesgo")
    private Integer calificacionRiesgo;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "verificador",
            nullable = false
    )
    private Empleado verificador;

    public Duenio() {
    }

    public Duenio(
            Persona persona,
            Empleado verificador
    ) {
        this.persona = persona;
        this.verificador = verificador;

        /*
         * Valores iniciales conservadores.
         *
         * El dueño se crea automáticamente cuando el producto
         * es incorporado a una subasta.
         */
        this.verificacionFinanciera = "si";
        this.verificacionJudicial = "si";
        this.calificacionRiesgo = 3;
    }

    public Duenio(
            Persona persona,
            Integer numeroPais,
            String verificacionFinanciera,
            String verificacionJudicial,
            Integer calificacionRiesgo,
            Empleado verificador
    ) {
        this.persona = persona;
        this.numeroPais = numeroPais;
        this.verificacionFinanciera =
                verificacionFinanciera;

        this.verificacionJudicial =
                verificacionJudicial;

        this.calificacionRiesgo =
                calificacionRiesgo;

        this.verificador = verificador;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public Persona getPersona() {
        return persona;
    }

    public Integer getNumeroPais() {
        return numeroPais;
    }

    public String getVerificacionFinanciera() {
        return verificacionFinanciera;
    }

    public String getVerificacionJudicial() {
        return verificacionJudicial;
    }

    public Integer getCalificacionRiesgo() {
        return calificacionRiesgo;
    }

    public Empleado getVerificador() {
        return verificador;
    }

    public void setNumeroPais(
            Integer numeroPais
    ) {
        this.numeroPais = numeroPais;
    }

    public void setVerificacionFinanciera(
            String verificacionFinanciera
    ) {
        this.verificacionFinanciera =
                verificacionFinanciera;
    }

    public void setVerificacionJudicial(
            String verificacionJudicial
    ) {
        this.verificacionJudicial =
                verificacionJudicial;
    }

    public void setCalificacionRiesgo(
            Integer calificacionRiesgo
    ) {
        this.calificacionRiesgo =
                calificacionRiesgo;
    }

    public void setVerificador(
            Empleado verificador
    ) {
        this.verificador = verificador;
    }
}