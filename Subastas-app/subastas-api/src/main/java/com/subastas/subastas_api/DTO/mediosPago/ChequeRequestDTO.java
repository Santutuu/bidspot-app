package com.subastas.subastas_api.DTO.mediosPago;

public class ChequeRequestDTO {

    private String identificacion;
    private String nroCheque;
    private String beneficiario;
    private String cuilCuit;
    private Float saldo;

    public String getIdentificacion() {
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

    public Float getSaldo() {
        return saldo;
    }
}