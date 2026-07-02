package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class SolicitudPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolicitud;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 2000, nullable = false)
    private String descripcion;

    @ElementCollection
    @CollectionTable(
            name = "solicitud_publicacion_imagenes",
            joinColumns = @JoinColumn(name = "solicitud_id")
    )
    @Column(name = "imagen_url")
    private List<String> imagenesUrl = new ArrayList<>();

    @Column(nullable = false)
    private boolean declaracionPropiedad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    @ElementCollection
    @CollectionTable(
            name = "solicitud_publicacion_acciones",
            joinColumns = @JoinColumn(name = "solicitud_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "accion")
    private List<AccionRequerida> accionesRequeridas = new ArrayList<>();

    @OneToMany(mappedBy = "solicitudPublicacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespuestaAccionRequerida> respuestasAcciones = new ArrayList<>();

    @Column(length = 1000)
    private String motivoRechazo;

    private String ubicacionDeposito;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaActualizacion;

    public SolicitudPublicacion() {
    }

    public SolicitudPublicacion(Usuario usuario,
                                Categoria categoria,
                                String titulo,
                                String descripcion,
                                List<String> imagenesUrl,
                                boolean declaracionPropiedad) {
        this.usuario = usuario;
        this.categoria = categoria;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagenesUrl = imagenesUrl;
        this.declaracionPropiedad = declaracionPropiedad;
        this.estado = EstadoSolicitud.PENDIENTE;
    }

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();

        if (estado == null) {
            estado = EstadoSolicitud.PENDIENTE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Long getIdSolicitud() {
        return idSolicitud;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Item getItem() {
        return item;
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

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public List<AccionRequerida> getAccionesRequeridas() {
        return accionesRequeridas;
    }

    public List<RespuestaAccionRequerida> getRespuestasAcciones() {
        return respuestasAcciones;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public String getUbicacionDeposito() {
        return ubicacionDeposito;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public String getPrimeraImagen() {
        if (imagenesUrl != null && !imagenesUrl.isEmpty()) {
            return imagenesUrl.get(0);
        }

        return null;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }

    public void setUbicacionDeposito(String ubicacionDeposito) {
        this.ubicacionDeposito = ubicacionDeposito;
    }

    public void agregarAccionRequerida(AccionRequerida accion) {
        this.accionesRequeridas.add(accion);
    }

    public void eliminarAccionRequerida(AccionRequerida accion) {
        this.accionesRequeridas.remove(accion);
    }
}