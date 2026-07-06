package com.subastas.subastas_api.DTO.subasta;

public class CierreLoteResponseDTO {

    private Long idSubasta;
    private Long idItemCatalogo;
    private Long idVenta;
    private Long idGanador;
    private String estadoLote;
    private String estadoSubasta;
    private Long idProximoLote;

    public CierreLoteResponseDTO(Long idSubasta,
                                 Long idItemCatalogo,
                                 Long idVenta,
                                 Long idGanador,
                                 String estadoLote,
                                 String estadoSubasta,
                                 Long idProximoLote) {
        this.idSubasta = idSubasta;
        this.idItemCatalogo = idItemCatalogo;
        this.idVenta = idVenta;
        this.idGanador = idGanador;
        this.estadoLote = estadoLote;
        this.estadoSubasta = estadoSubasta;
        this.idProximoLote = idProximoLote;
    }

    public Long getIdSubasta() { return idSubasta; }
    public Long getIdItemCatalogo() { return idItemCatalogo; }
    public Long getIdVenta() { return idVenta; }
    public Long getIdGanador() { return idGanador; }
    public String getEstadoLote() { return estadoLote; }
    public String getEstadoSubasta() { return estadoSubasta; }
    public Long getIdProximoLote() { return idProximoLote; }
}