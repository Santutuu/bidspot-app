package com.subastas.subastas_api.DTO.publicacion;

import com.subastas.subastas_api.model.Categoria;

import java.util.ArrayList;
import java.util.List;

public class SolicitudPublicacionRequestDTO {

    private Categoria categoria;

    private String titulo;

    private String descripcion;

    private List<String> imagenesUrl =
            new ArrayList<>();

    private boolean declaracionPropiedad;

    public SolicitudPublicacionRequestDTO() {
    }

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

    public void setCategoria(
            Categoria categoria
    ) {
        this.categoria = categoria;
    }

    public void setTitulo(
            String titulo
    ) {
        this.titulo = titulo;
    }

    public void setDescripcion(
            String descripcion
    ) {
        this.descripcion = descripcion;
    }

    public void setImagenesUrl(
            List<String> imagenesUrl
    ) {
        this.imagenesUrl =
                imagenesUrl != null
                        ? new ArrayList<>(imagenesUrl)
                        : new ArrayList<>();
    }

    public void setDeclaracionPropiedad(
            boolean declaracionPropiedad
    ) {
        this.declaracionPropiedad =
                declaracionPropiedad;
    }
}