package com.subastas.subastas_api.DTO;

import com.subastas.subastas_api.model.Subasta;

public class SubastaHomeDTO {

    private Long id;
    private String titulo;
    private String moneda;
    private Float precioActual;
    private boolean precioVisible;
    private String imagenUrl;

    public SubastaHomeDTO(Long id,
                          String titulo,
                          String moneda,
                          Float precioActual,
                          boolean precioVisible,
                          String imagenUrl) {
        this.id = id;
        this.titulo = titulo;
        this.moneda = moneda;
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
                precioActual,
                usuarioAutenticado,
                subasta.getItem().getPrimeraImagen()
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

    public Float getPrecioActual() {
        return precioActual;
    }

    public boolean isPrecioVisible() {
        return precioVisible;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public void setPrecioActual(Float precioActual) {
        this.precioActual = precioActual;
    }

    public void setPrecioVisible(boolean precioVisible) {
        this.precioVisible = precioVisible;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}