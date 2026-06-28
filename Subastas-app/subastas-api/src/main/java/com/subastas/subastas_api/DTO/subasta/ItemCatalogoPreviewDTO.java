package com.subastas.subastas_api.DTO.subasta;

public class ItemCatalogoPreviewDTO {

    private Long idItemCatalogo;
    private Integer numeroLote;
    private String titulo;
    private String imagenUrl;
    private Float precioBase;

    public ItemCatalogoPreviewDTO(Long idItemCatalogo,
                                  Integer numeroLote,
                                  String titulo,
                                  String imagenUrl,
                                  Float precioBase) {
        this.idItemCatalogo = idItemCatalogo;
        this.numeroLote = numeroLote;
        this.titulo = titulo;
        this.imagenUrl = imagenUrl;
        this.precioBase = precioBase;
    }

    public Long getIdItemCatalogo() {
        return idItemCatalogo;
    }

    public Integer getNumeroLote() {
        return numeroLote;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public Float getPrecioBase() {
        return precioBase;
    }
}