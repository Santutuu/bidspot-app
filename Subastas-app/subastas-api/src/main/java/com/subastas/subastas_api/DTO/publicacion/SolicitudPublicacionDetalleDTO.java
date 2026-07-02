package com.subastas.subastas_api.DTO.publicacion;

import com.subastas.subastas_api.model.AccionRequerida;

import java.time.LocalDateTime;
import java.util.List;

public class SolicitudPublicacionDetalleDTO {

    private Long idSolicitud;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String estado;
    private List<String> imagenesUrl;
    private boolean declaracionPropiedad;
    private List<AccionRequerida> accionesRequeridas;
    private List<RespuestaAccionDTO> respuestasAcciones;
    private String motivoRechazo;
    private String ubicacionDeposito;

    private Long idSubasta;
    private String tituloSubasta;
    private LocalDateTime fechaSubasta;
    private String ubicacionSubasta;

    public SolicitudPublicacionDetalleDTO(Long idSolicitud,
                                          String titulo,
                                          String descripcion,
                                          String categoria,
                                          String estado,
                                          List<String> imagenesUrl,
                                          boolean declaracionPropiedad,
                                          List<AccionRequerida> accionesRequeridas,
                                          List<RespuestaAccionDTO> respuestasAcciones,
                                          String motivoRechazo,
                                          String ubicacionDeposito,
                                          Long idSubasta,
                                          String tituloSubasta,
                                          LocalDateTime fechaSubasta,
                                          String ubicacionSubasta) {
        this.idSolicitud = idSolicitud;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.estado = estado;
        this.imagenesUrl = imagenesUrl;
        this.declaracionPropiedad = declaracionPropiedad;
        this.accionesRequeridas = accionesRequeridas;
        this.respuestasAcciones = respuestasAcciones;
        this.motivoRechazo = motivoRechazo;
        this.ubicacionDeposito = ubicacionDeposito;
        this.idSubasta = idSubasta;
        this.tituloSubasta = tituloSubasta;
        this.fechaSubasta = fechaSubasta;
        this.ubicacionSubasta = ubicacionSubasta;
    }

    public Long getIdSolicitud() {
        return idSolicitud;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getEstado() {
        return estado;
    }

    public List<String> getImagenesUrl() {
        return imagenesUrl;
    }

    public boolean isDeclaracionPropiedad() {
        return declaracionPropiedad;
    }

    public List<AccionRequerida> getAccionesRequeridas() {
        return accionesRequeridas;
    }

    public List<RespuestaAccionDTO> getRespuestasAcciones() {
        return respuestasAcciones;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public String getUbicacionDeposito() {
        return ubicacionDeposito;
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public String getTituloSubasta() {
        return tituloSubasta;
    }

    public LocalDateTime getFechaSubasta() {
        return fechaSubasta;
    }

    public String getUbicacionSubasta() {
        return ubicacionSubasta;
    }
}
