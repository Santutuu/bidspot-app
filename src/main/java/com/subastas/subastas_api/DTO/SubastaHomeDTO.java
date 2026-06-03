package com.subastas.subastas_api.DTO;

import com.subastas.subastas_api.model.Subasta;

public class SubastaHomeDTO {

    private Long id;
    private String titulo;
    private String moneda;
    private String categoriaMin;
    private Float precioActual;
    private boolean precioVisible;
    private String imagenUrl;

    public SubastaHomeDTO(Long id,
                          String titulo,
                          String moneda,
                          String categoriaMin,
                          Float precioActual,
                          boolean precioVisible,
                          String imagenUrl) {

        this.id = id;
        this.titulo = titulo;
        this.moneda = moneda;
        this.categoriaMin = categoriaMin;
        this.precioActual = precioActual;
        this.precioVisible = precioVisible;
        this.imagenUrl = imagenUrl;
    }

    public static SubastaHomeDTO fromEntity(Subasta subasta, boolean usuarioAutenticado) {

        Float precioActual = null;

        if (usuarioAutenticado) {
            if (subasta.getPujaActual() != null) {
                precioActual = subasta.getPujaActual().getMonto();
            } else {
                precioActual = subasta.getPrecioInicial();
            }
        }

        return new SubastaHomeDTO(
                subasta.getIdSubasta(),
                subasta.getItem().getTitulo(),
                subasta.getMoneda().toString(),
                subasta.getCategoriaMin().toString(),
                precioActual,
                usuarioAutenticado,
                subasta.getItem().getImagenUrl(0)
        );
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getMoneda() {
        return moneda;
    }

    public String getCategoriaMin() {
        return categoriaMin;
    }

    public Float getPrecioActual() {
        return precioActual;
    }

    public boolean isPrecioVisible() {
        return precioVisible;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }
}