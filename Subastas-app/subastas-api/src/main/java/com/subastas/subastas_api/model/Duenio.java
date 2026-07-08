package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "duenios")
public class Duenio {

    @Id
    @Column(name = "identificador")
    private Long identificador;

    @Column(name = "numeropais")
    private Integer numeroPais;

    @Column(name = "verificaciónfinanciera")
    private String verificacionFinanciera;

    @Column(name = "verificaciónjudicial")
    private String verificacionJudicial;

    @Column(name = "calificacionriesgo")
    private Integer calificacionRiesgo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verificador", nullable = false)
    private Empleado verificador;

    public Duenio() {
    }

    public Long getIdentificador() {
        return identificador;
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
}