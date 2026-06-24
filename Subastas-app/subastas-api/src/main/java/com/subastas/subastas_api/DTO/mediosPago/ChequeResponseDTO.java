

package com.subastas.subastas_api.DTO.mediosPago;

public class ChequeResponseDTO {

    private Long idMedioPago;
    private String tipo;
    private int identificacion;
    private String nroCheque;
    private String beneficiario;
    private String cuilCuit;
    private float saldo;

    public ChequeResponseDTO(Long idMedioPago,
                             int identificacion,
                             String nroCheque,
                             String beneficiario,
                             String cuilCuit,
                             float saldo) {
        this.idMedioPago = idMedioPago;
        this.tipo = "CHEQUE";
        this.identificacion = identificacion;
        this.nroCheque = nroCheque;
        this.beneficiario = beneficiario;
        this.cuilCuit = cuilCuit;
        this.saldo = saldo;
    }

    public Long getIdMedioPago() {
        return idMedioPago;
    }

    public String getTipo() {
        return tipo;
    }

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