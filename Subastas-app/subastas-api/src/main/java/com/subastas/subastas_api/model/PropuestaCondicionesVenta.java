package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "propuesta_condiciones_venta")
public class PropuestaCondicionesVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_propuesta")
    private Long idPropuesta;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "solicitud_id",
            nullable = false,
            unique = true
    )
    private SolicitudPublicacion solicitud;

    /*
     * La subasta pertenece al modelo legacy:
     * Subasta Java → tabla subastas.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subasta_id", nullable = false)
    private Subasta subasta;

    @Column(name = "precio_base", nullable = false)
    private Float precioBase;

    /*
     * Porcentaje. Por ejemplo, 10 representa 10 %.
     */
    @Column(name = "porcentaje_comision", nullable = false)
    private Float porcentajeComision;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPropuestaVenta estado =
            EstadoPropuestaVenta.PENDIENTE;

    @Column(name = "motivo_rechazo_usuario", length = 1000)
    private String motivoRechazoUsuario;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    public PropuestaCondicionesVenta() {
    }

    public PropuestaCondicionesVenta(
            SolicitudPublicacion solicitud,
            Subasta subasta,
            Float precioBase,
            Float porcentajeComision
    ) {
        this.solicitud = solicitud;
        this.subasta = subasta;
        this.precioBase = precioBase;
        this.porcentajeComision = porcentajeComision;
        this.estado = EstadoPropuestaVenta.PENDIENTE;
        this.fechaCreacion = LocalDateTime.now();
    }

    @PrePersist
    private void prePersist() {
        if (estado == null) {
            estado = EstadoPropuestaVenta.PENDIENTE;
        }

        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public void aceptar() {
        if (estado != EstadoPropuestaVenta.PENDIENTE) {
            throw new IllegalStateException(
                    "La propuesta ya fue respondida"
            );
        }

        this.estado = EstadoPropuestaVenta.ACEPTADA;
        this.fechaRespuesta = LocalDateTime.now();
        this.motivoRechazoUsuario = null;
    }

    public void rechazar(String motivo) {
        if (estado != EstadoPropuestaVenta.PENDIENTE) {
            throw new IllegalStateException(
                    "La propuesta ya fue respondida"
            );
        }

        this.estado = EstadoPropuestaVenta.RECHAZADA;
        this.motivoRechazoUsuario = motivo;
        this.fechaRespuesta = LocalDateTime.now();
    }

    public Long getIdPropuesta() {
        return idPropuesta;
    }

    public SolicitudPublicacion getSolicitud() {
        return solicitud;
    }

    public Subasta getSubasta() {
        return subasta;
    }

    public Float getPrecioBase() {
        return precioBase;
    }

    public Float getPorcentajeComision() {
        return porcentajeComision;
    }

    public EstadoPropuestaVenta getEstado() {
        return estado;
    }

    public String getMotivoRechazoUsuario() {
        return motivoRechazoUsuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }
}