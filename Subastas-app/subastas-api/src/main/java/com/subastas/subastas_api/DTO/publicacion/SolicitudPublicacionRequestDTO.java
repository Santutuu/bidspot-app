package com.subastas.subastas_api.DTO.publicacion;

import com.subastas.subastas_api.model.Categoria;

import java.util.List;

public class SolicitudPublicacionRequestDTO {

    private Categoria categoria;
    private String titulo;
    private String descripcion;
    private List<String> imagenesUrl;
    private boolean declaracionPropiedad;

    public Categoria getCategoria() {
        return categoria;
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

    public boolean isDeclaracionPropiedad() {
        return declaracionPropiedad;
    }
}