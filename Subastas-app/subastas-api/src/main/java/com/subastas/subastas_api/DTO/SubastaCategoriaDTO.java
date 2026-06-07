package com.subastas.subastas_api.DTO;

public class SubastaCategoriaDTO {

    private Long idSubasta;
    private String titulo;
    private String imagenUrl;
    private Float precio;
    private String moneda;

    public SubastaCategoriaDTO(Long idSubasta,
                               String titulo,
                               String imagenUrl,
                               Float precio,
                               String moneda) {
        this.idSubasta = idSubasta;
        this.titulo = titulo;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
        this.moneda = moneda;
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public Float getPrecio() {
        return precio;
    }

    public String getMoneda() {
        return moneda;
    }
}