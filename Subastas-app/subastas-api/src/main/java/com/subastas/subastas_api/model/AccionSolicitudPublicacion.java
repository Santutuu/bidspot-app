package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "accion_solicitud_publicacion")
public class AccionSolicitudPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_accion")
    private Long idAccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private SolicitudPublicacion solicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoAccionSolicitud tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoAccionSolicitud estado =
            EstadoAccionSolicitud.PENDIENTE;

    /*
     * Textos visibles para el usuario.
     */
    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "descripcion", length = 1500)
    private String descripcion;

    /*
     * Respuesta del usuario.
     */
    @Column(name = "aceptada")
    private Boolean aceptada;

    @Column(name = "comentario_respuesta", length = 1500)
    private String comentarioRespuesta;

    /*
     * Será útil posteriormente para acreditar origen lícito.
     */
    @Column(name = "archivo_url", length = 1000)
    private String archivoUrl;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    public AccionSolicitudPublicacion() {
    }

    public AccionSolicitudPublicacion(
            SolicitudPublicacion solicitud,
            TipoAccionSolicitud tipo,
            String titulo,
            String descripcion
    ) {
        this.solicitud = solicitud;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estado = EstadoAccionSolicitud.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    @PrePersist
    private void prePersist() {
        if (estado == null) {
            estado = EstadoAccionSolicitud.PENDIENTE;
        }

        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public void completar(
            Boolean aceptada,
            String comentarioRespuesta
    ) {
        if (estado != EstadoAccionSolicitud.PENDIENTE) {
            throw new IllegalStateException(
                    "La acción ya no se encuentra pendiente"
            );
        }

        this.aceptada = aceptada;
        this.comentarioRespuesta = comentarioRespuesta;
        this.estado = EstadoAccionSolicitud.COMPLETADA;
        this.fechaResolucion = LocalDateTime.now();
    }

    public void completarConArchivo(
            String archivoUrl,
            String comentarioRespuesta
    ) {
        if (estado != EstadoAccionSolicitud.PENDIENTE) {
            throw new IllegalStateException(
                    "La acción ya no se encuentra pendiente"
            );
        }

        this.archivoUrl = archivoUrl;
        this.comentarioRespuesta = comentarioRespuesta;
        this.estado = EstadoAccionSolicitud.COMPLETADA;
        this.fechaResolucion = LocalDateTime.now();
    }

    public void cancelar() {
        if (estado == EstadoAccionSolicitud.COMPLETADA) {
            throw new IllegalStateException(
                    "No se puede cancelar una acción completada"
            );
        }

        this.estado = EstadoAccionSolicitud.CANCELADA;
        this.fechaResolucion = LocalDateTime.now();
    }

    public boolean estaPendiente() {
        return estado == EstadoAccionSolicitud.PENDIENTE;
    }

    public Long getIdAccion() {
        return idAccion;
    }

    public SolicitudPublicacion getSolicitud() {
        return solicitud;
    }

    public TipoAccionSolicitud getTipo() {
        return tipo;
    }

    public EstadoAccionSolicitud getEstado() {
        return estado;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Boolean getAceptada() {
        return aceptada;
    }

    public String getComentarioRespuesta() {
        return comentarioRespuesta;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }
}