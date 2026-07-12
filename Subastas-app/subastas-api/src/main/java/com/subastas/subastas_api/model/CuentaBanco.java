package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
public class CuentaBanco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCuentaBanco;

    @Column(nullable = false, unique = true)
    private String cbu;

    @Column(nullable = false)
    private String banco;

    @Column(nullable = false)
    private String titular;

    public CuentaBanco() {
    }

    public CuentaBanco(String cbu,
                       String banco,
                       String titular) {
        this.cbu = cbu;
        this.banco = banco;
        this.titular = titular;
    }

    public Long getIdCuentaBanco() {
        return idCuentaBanco;
    }

    public String getCbu() {
        return cbu;
    }

    public String getBanco() {
        return banco;
    }

    public String getTitular() {
        return titular;
    }

    public void setCbu(String cbu) {
        this.cbu = cbu;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }
}