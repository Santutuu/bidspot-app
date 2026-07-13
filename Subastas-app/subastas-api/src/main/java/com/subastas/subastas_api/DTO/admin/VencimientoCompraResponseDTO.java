package com.subastas.subastas_api.DTO.admin;

public class VencimientoCompraResponseDTO {

    private Integer ventasProcesadas;

    private Integer usuariosBloqueados;

    public VencimientoCompraResponseDTO() {
    }

    public VencimientoCompraResponseDTO(
            Integer ventasProcesadas,
            Integer usuariosBloqueados
    ) {
        this.ventasProcesadas =
                ventasProcesadas;

        this.usuariosBloqueados =
                usuariosBloqueados;
    }

    public Integer getVentasProcesadas() {
        return ventasProcesadas;
    }

    public Integer getUsuariosBloqueados() {
        return usuariosBloqueados;
    }
}