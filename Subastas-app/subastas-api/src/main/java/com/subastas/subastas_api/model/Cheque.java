package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
public class Cheque extends MedioDePago {

    @Column(nullable = false)
    private String identificacion;

    @Column(nullable = false)
    private String nroCheque;

    @Column(nullable = false)
    private String beneficiario;

    @Column(nullable = false)
    private String cuilCuit;

    @Column(nullable = false)
    private Float saldo;

    public Cheque() {
    }

    public Cheque(
            Cliente cliente,
            String identificacion,
            String nroCheque,
            String beneficiario,
            String cuilCuit,
            Float saldo,
            Moneda moneda
    ) {
        super(cliente, moneda);

        this.identificacion = identificacion;
        this.nroCheque = nroCheque;
        this.beneficiario = beneficiario;
        this.cuilCuit = cuilCuit;
        this.saldo = saldo;
    }

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

    public boolean tieneSaldoSuficiente(
            Float monto
    ) {
        return monto != null
                && monto > 0f
                && saldo != null
                && saldo >= monto;
    }

    public void consumirSaldo(
            Float monto
    ) {
        if (!tieneSaldoSuficiente(monto)) {
            throw new IllegalStateException(
                    "El cheque no tiene saldo suficiente"
            );
        }

        saldo -= monto;
    }
}