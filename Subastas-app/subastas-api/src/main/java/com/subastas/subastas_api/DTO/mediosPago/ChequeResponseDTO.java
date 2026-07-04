package com.subastas.subastas_api.DTO.mediosPago;

public class ChequeResponseDTO {

    private Long idCheque;
    private String identificacion;
    private String nroCheque;
    private String beneficiario;
    private String cuilCuit;
    private Float saldo;

    public ChequeResponseDTO(Long idCheque,
                             String identificacion,
                             String nroCheque,
                             String beneficiario,
                             String cuilCuit,
                             Float saldo) {
        this.idCheque = idCheque;
        this.identificacion = identificacion;
        this.nroCheque = nroCheque;
        this.beneficiario = beneficiario;
        this.cuilCuit = cuilCuit;
        this.saldo = saldo;
    }

    public Long getIdCheque() {
        return idCheque;
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
}