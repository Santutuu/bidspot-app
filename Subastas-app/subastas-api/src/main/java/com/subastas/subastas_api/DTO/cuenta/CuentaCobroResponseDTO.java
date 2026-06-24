package com.subastas.subastas_api.DTO.cuenta;

public class CuentaCobroResponseDTO {

    private Long idCuentaBanco;
    private String cbu;
    private String banco;
    private String titular;

    public CuentaCobroResponseDTO(Long idCuentaBanco,
                                  String cbu,
                                  String banco,
                                  String titular) {
        this.idCuentaBanco = idCuentaBanco;
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
}