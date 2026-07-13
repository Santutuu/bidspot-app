package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "devolucion_solicitud")
public class DevolucionSolicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_devolucion")
    private Long idDevolucion;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "solicitud_id",
            nullable = false,
            unique = true
    )
    private SolicitudPublicacion solicitud;

    @Column(name = "costo", nullable = false)
    private Float costo;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda", nullable = false)
    private Moneda moneda;

    @Column(name = "direccion_destino", length = 500)
    private String direccionDestino;

    /*
     * MedioDePago ya debe encontrarse relacionado con Cliente.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medio_pago_id")
    private MedioDePago medioPago;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoDevolucion estado =
            EstadoDevolucion.PENDIENTE_CONFIGURACION;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    public DevolucionSolicitud() {
    }

    public DevolucionSolicitud(
            SolicitudPublicacion solicitud,
            Float costo,
            Moneda moneda
    ) {
        this.solicitud = solicitud;
        this.costo = costo;
        this.moneda = moneda;
        this.estado = EstadoDevolucion.PENDIENTE_CONFIGURACION;
        this.fechaCreacion = LocalDateTime.now();
    }

    @PrePersist
    private void prePersist() {
        if (estado == null) {
            estado = EstadoDevolucion.PENDIENTE_CONFIGURACION;
        }

        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public void configurar(
            String direccionDestino,
            MedioDePago medioPago
    ) {
        if (estado != EstadoDevolucion.PENDIENTE_CONFIGURACION
                && estado != EstadoDevolucion.PENDIENTE_PAGO) {

            throw new IllegalStateException(
                    "La devolución ya no puede modificarse"
            );
        }

        this.direccionDestino = direccionDestino;
        this.medioPago = medioPago;
        this.estado = EstadoDevolucion.PENDIENTE_PAGO;
    }

    public void confirmarPago() {
        if (estado != EstadoDevolucion.PENDIENTE_PAGO) {
            throw new IllegalStateException(
                    "La devolución no está pendiente de pago"
            );
        }

        if (direccionDestino == null
                || direccionDestino.isBlank()) {

            throw new IllegalStateException(
                    "La devolución no tiene dirección configurada"
            );
        }

        if (medioPago == null) {
            throw new IllegalStateException(
                    "La devolución no tiene medio de pago configurado"
            );
        }

        this.estado = EstadoDevolucion.PAGO_CONFIRMADO;
        this.fechaPago = LocalDateTime.now();
    }

    public void marcarEnviada() {
        if (estado != EstadoDevolucion.PAGO_CONFIRMADO) {
            throw new IllegalStateException(
                    "La devolución todavía no fue pagada"
            );
        }

        this.estado = EstadoDevolucion.ENVIADA;
        this.fechaEnvio = LocalDateTime.now();
    }

    public void marcarEntregada() {
        if (estado != EstadoDevolucion.ENVIADA) {
            throw new IllegalStateException(
                    "La devolución todavía no fue enviada"
            );
        }

        this.estado = EstadoDevolucion.ENTREGADA;
        this.fechaEntrega = LocalDateTime.now();
    }

    public void cancelar() {
        if (estado == EstadoDevolucion.ENTREGADA) {
            throw new IllegalStateException(
                    "No se puede cancelar una devolución entregada"
            );
        }

        this.estado = EstadoDevolucion.CANCELADA;
    }

    public Long getIdDevolucion() {
        return idDevolucion;
    }

    public SolicitudPublicacion getSolicitud() {
        return solicitud;
    }

    public Float getCosto() {
        return costo;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public String getDireccionDestino() {
        return direccionDestino;
    }

    public MedioDePago getMedioPago() {
        return medioPago;
    }

    public EstadoDevolucion getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }
}