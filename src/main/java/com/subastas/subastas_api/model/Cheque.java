package com.subastas.subastas_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
public class Cheque extends MedioDePago {

    private String identificacion;
    private int nroCheque;
    private String beneficiario;
    private int cuilCuit;
    private float saldo;

    public Cheque() {
        super();
    }

    public Cheque(
            Usuario usuario,
            String identificacion,
            int nroCheque,
            String beneficiario,
            int cuilCuit, float saldo) {

        super(usuario);

        this.identificacion = identificacion;
        this.nroCheque = nroCheque;
        this.beneficiario = beneficiario;
        this.cuilCuit = cuilCuit;
        this.saldo = saldo;
    }



    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public int getNroCheque() {
        return nroCheque;
    }

    public void setNroCheque(int nroCheque) {
        this.nroCheque = nroCheque;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public int getCuilCuit() {
        return cuilCuit;
    }

    public void setCuilCuit(int cuilCuit) {
        this.cuilCuit = cuilCuit;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }
}