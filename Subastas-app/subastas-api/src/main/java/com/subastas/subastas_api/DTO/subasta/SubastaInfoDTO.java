package com.subastas.subastas_api.DTO.subasta;

import com.subastas.subastas_api.model.CategoriaUsuario;
import com.subastas.subastas_api.model.EstadoSubasta;

import java.time.LocalDateTime;

public class SubastaInfoDTO {

    private Long idSubasta;
    private String titulo;
    private EstadoSubasta estadoSubasta;
    private CategoriaUsuario categoriaMin;
    private String moneda;
    private LocalDateTime fechaInicio;
    private String ubicacion;
    private String rematador;
    private String linkVivo;
    private boolean guardada;

    public SubastaInfoDTO(Long idSubasta,
                          String titulo,
                          EstadoSubasta estadoSubasta,
                          CategoriaUsuario categoriaMin,
                          String moneda,
                          LocalDateTime fechaInicio,
                          String ubicacion,
                          String rematador,
                          String linkVivo,
                          boolean guardada) {
        this.idSubasta = idSubasta;
        this.titulo = titulo;
        this.estadoSubasta = estadoSubasta;
        this.categoriaMin = categoriaMin;
        this.moneda = moneda;
        this.fechaInicio = fechaInicio;
        this.ubicacion = ubicacion;
        this.rematador = rematador;
        this.linkVivo = linkVivo;
        this.guardada = guardada;
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public String getTitulo() {
        return titulo;
    }

    public EstadoSubasta getEstadoSubasta() {
        return estadoSubasta;
    }

    public CategoriaUsuario getCategoriaMin() {
        return categoriaMin;
    }

    public String getMoneda() {
        return moneda;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getRematador() {
        return rematador;
    }

    public String getLinkVivo() {
        return linkVivo;
    }

    public boolean isGuardada() {
        return guardada;
    }
}