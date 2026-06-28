package com.subastas.subastas_api.DTO.subasta;

import java.util.List;

public class ItemActualDTO {

    private Long idItemCatalogo;
    private Integer numeroLote;
    private String titulo;
    private String descripcion;
    private List<String> imagenesUrl;
    private Float precioBase;
    private Float precioActual;

    public ItemActualDTO(Long idItemCatalogo,
                         Integer numeroLote,
                         String titulo,
                         String descripcion,
                         List<String> imagenesUrl,
                         Float precioBase,
                         Float precioActual) {
        this.idItemCatalogo = idItemCatalogo;
        this.numeroLote = numeroLote;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagenesUrl = imagenesUrl;
        this.precioBase = precioBase;
        this.precioActual = precioActual;
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

    public String getDescripcion() {
        return descripcion;
    }

    public List<String> getImagenesUrl() {
        return imagenesUrl;
    }

    public Float getPrecioBase() {
        return precioBase;
    }

    public Float getPrecioActual() {
        return precioActual;
    }
}