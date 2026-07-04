package com.subastas.subastas_api.DTO.cuenta;

public class CuentaBancoRequestDTO {

    private String cbu;
    private String banco;
    private String titular;

    public String getCbu() {
        return cbu;
    }

    public String getBanco() {
        return banco;
    }

    public String getTitular() {
        return titular;
    }
}