package com.subastas.subastas_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
public class Cheque extends MedioDePago {

    private int identificacion;
    private String nroCheque;
    private String beneficiario;
    private String cuilCuit;
    private float saldo;

    public Cheque() {
        super();
    }

    public Cheque(
            Usuario usuario,
            int identificacion,
            String nroCheque,
            String beneficiario,
            String cuilCuit, float saldo) {

        super(usuario);

        this.identificacion = identificacion;
        this.nroCheque = nroCheque;
        this.beneficiario = beneficiario;
        this.cuilCuit = cuilCuit;
        this.saldo = saldo;
    }



    public int getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(int identificacion) {
        this.identificacion = identificacion;
    }

    public String getNroCheque() {
        return nroCheque;
    }

    public void setNroCheque(String nroCheque) {
        this.nroCheque = nroCheque;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public String getCuilCuit() {
        return cuilCuit;
    }

    public void setCuilCuit(String cuilCuit) {
        this.cuilCuit = cuilCuit;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }
}