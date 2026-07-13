package com.subastas.subastas_api.DTO.subasta;

public class ActivarSubastaResponseDTO {

    private Long idSubasta;

    private String estadoSubasta;

    private Long idCatalogo;

    private String descripcionCatalogo;

    private Long idItemCatalogo;

    private Long idProducto;

    private String tituloProducto;

    private String estadoLote;

    private String mensaje;

    public ActivarSubastaResponseDTO() {
    }

    public ActivarSubastaResponseDTO(
            Long idSubasta,
            String estadoSubasta,
            Long idCatalogo,
            String descripcionCatalogo,
            Long idItemCatalogo,
            Long idProducto,
            String tituloProducto,
            String estadoLote,
            String mensaje
    ) {
        this.idSubasta = idSubasta;
        this.estadoSubasta = estadoSubasta;
        this.idCatalogo = idCatalogo;
        this.descripcionCatalogo =
                descripcionCatalogo;

        this.idItemCatalogo =
                idItemCatalogo;

        this.idProducto = idProducto;
        this.tituloProducto = tituloProducto;
        this.estadoLote = estadoLote;
        this.mensaje = mensaje;
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public String getEstadoSubasta() {
        return estadoSubasta;
    }

    public Long getIdCatalogo() {
        return idCatalogo;
    }

    public String getDescripcionCatalogo() {
        return descripcionCatalogo;
    }

    public Long getIdItemCatalogo() {
        return idItemCatalogo;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public String getTituloProducto() {
        return tituloProducto;
    }

    public String getEstadoLote() {
        return estadoLote;
    }

    public String getMensaje() {
        return mensaje;
    }
}