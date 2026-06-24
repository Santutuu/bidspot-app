package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;



@Entity
@Getter
@Setter
public class CuentaBanco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCuentaBanco;

    private String cbu; // pendiente cambiarlo a String
    private String banco;
    private String titular;

    public CuentaBanco() {}

    public CuentaBanco(String cbu, String banco, String titular) {
        this.cbu = cbu;
        this.banco = banco;
        this.titular = titular;
    }

    public Long getIdCuentaBanco() { return idCuentaBanco; }

    public String getCbu() { return cbu; }
    public void setCbu(String cbu) { this.cbu = cbu; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }
}