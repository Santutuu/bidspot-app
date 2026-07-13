package com.subastas.subastas_api.DTO.publicacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SolicitudPublicacionDetalleDTO {

    private Long idSolicitud;

    private String titulo;

    private String descripcion;

    private String categoria;

    private String estado;

    private List<String> imagenesUrl =
            new ArrayList<>();

    private boolean declaracionPropiedad;

    private String direccionDeposito;

    private LocalDate fechaLimiteEnvio;

    private Boolean aceptaDevolucionConCargo;

    private LocalDateTime fechaRecepcion;

    private String ubicacionActual;

    private LocalDateTime fechaActualizacionUbicacion;

    private String motivoRechazo;

    private List<AccionSolicitudDTO> accionesPendientes =
            new ArrayList<>();

    private List<AccionSolicitudDTO> accionesCompletadas =
            new ArrayList<>();

    private PropuestaCondicionesVentaDTO propuestaVenta;

    private DevolucionSolicitudDTO devolucion;

    private Long idItem;

    private Long idSubasta;

    private String tituloSubasta;

    private LocalDateTime fechaSubasta;

    private String ubicacionSubasta;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    public SolicitudPublicacionDetalleDTO() {
    }

    public SolicitudPublicacionDetalleDTO(
            Long idSolicitud,
            String titulo,
            String descripcion,
            String categoria,
            String estado,
            List<String> imagenesUrl,
            boolean declaracionPropiedad,
            String direccionDeposito,
            LocalDate fechaLimiteEnvio,
            Boolean aceptaDevolucionConCargo,
            LocalDateTime fechaRecepcion,
            String ubicacionActual,
            LocalDateTime fechaActualizacionUbicacion,
            String motivoRechazo,
            List<AccionSolicitudDTO> accionesPendientes,
            List<AccionSolicitudDTO> accionesCompletadas,
            PropuestaCondicionesVentaDTO propuestaVenta,
            DevolucionSolicitudDTO devolucion,
            Long idItem,
            Long idSubasta,
            String tituloSubasta,
            LocalDateTime fechaSubasta,
            String ubicacionSubasta,
            LocalDateTime fechaCreacion,
            LocalDateTime fechaActualizacion
    ) {
        this.idSolicitud = idSolicitud;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.estado = estado;
        this.imagenesUrl = imagenesUrl;
        this.declaracionPropiedad =
                declaracionPropiedad;
        this.direccionDeposito =
                direccionDeposito;
        this.fechaLimiteEnvio =
                fechaLimiteEnvio;
        this.aceptaDevolucionConCargo =
                aceptaDevolucionConCargo;
        this.fechaRecepcion = fechaRecepcion;
        this.ubicacionActual = ubicacionActual;
        this.fechaActualizacionUbicacion =
                fechaActualizacionUbicacion;
        this.motivoRechazo = motivoRechazo;
        this.accionesPendientes =
                accionesPendientes;
        this.accionesCompletadas =
                accionesCompletadas;
        this.propuestaVenta = propuestaVenta;
        this.devolucion = devolucion;
        this.idItem = idItem;
        this.idSubasta = idSubasta;
        this.tituloSubasta = tituloSubasta;
        this.fechaSubasta = fechaSubasta;
        this.ubicacionSubasta =
                ubicacionSubasta;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion =
                fechaActualizacion;
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

    public String getDireccionDeposito() {
        return direccionDeposito;
    }

    public LocalDate getFechaLimiteEnvio() {
        return fechaLimiteEnvio;
    }

    public Boolean getAceptaDevolucionConCargo() {
        return aceptaDevolucionConCargo;
    }

    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public String getUbicacionActual() {
        return ubicacionActual;
    }

    public LocalDateTime getFechaActualizacionUbicacion() {
        return fechaActualizacionUbicacion;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public List<AccionSolicitudDTO> getAccionesPendientes() {
        return accionesPendientes;
    }

    public List<AccionSolicitudDTO> getAccionesCompletadas() {
        return accionesCompletadas;
    }

    public PropuestaCondicionesVentaDTO getPropuestaVenta() {
        return propuestaVenta;
    }

    public DevolucionSolicitudDTO getDevolucion() {
        return devolucion;
    }

    public Long getIdItem() {
        return idItem;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}