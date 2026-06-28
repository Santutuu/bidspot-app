package com.subastas.subastas_api.DTO.subasta;

import com.subastas.subastas_api.model.CategoriaUsuario;
import com.subastas.subastas_api.model.EstadoSubasta;

import java.time.LocalDateTime;

public class SubastaHomeDTO {

    private Long idSubasta;
    private String titulo;
    private String imagenUrl;
    private Float precio;
    private String moneda;
    private EstadoSubasta estadoSubasta;
    private CategoriaUsuario categoriaMin;
    private LocalDateTime fechaInicio;

    public SubastaHomeDTO(Long idSubasta,
                          String titulo,
                          String imagenUrl,
                          Float precio,
                          String moneda,
                          EstadoSubasta estadoSubasta,
                          CategoriaUsuario categoriaMin,
                          LocalDateTime fechaInicio) {
        this.idSubasta = idSubasta;
        this.titulo = titulo;
        this.imagenUrl = imagenUrl;
        this.precio = precio;
        this.moneda = moneda;
        this.estadoSubasta = estadoSubasta;
        this.categoriaMin = categoriaMin;
        this.fechaInicio = fechaInicio;
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

    public EstadoSubasta getEstadoSubasta() {
        return estadoSubasta;
    }

    public CategoriaUsuario getCategoriaMin() {
        return categoriaMin;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }
}