package com.subastas.subastas_api.DTO.mediosPago;

public class ChequeRequestDTO {

    private int identificacion;
    private String nroCheque;
    private String beneficiario;
    private String cuilCuit;
    private float saldo;

    public int getIdentificacion() {
        return identificacion;
    }

    public String getNroCheque() {
        return nroCheque;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public String getCuilCuit() {
        return cuilCuit;
    }

    public float getSaldo() {
        return saldo;
    }
}