package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "penalizaciones",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_penalizaciones_venta_tipo",
                        columnNames = {
                                "venta_id",
                                "tipo"
                        }
                )
        }
)
public class Penalizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_penalizacion")
    private Long idPenalizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "cliente_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_penalizaciones_cliente"
            )
    )
    private Cliente cliente;

    /*
     * Una Venta puede originar una penalización por falta de fondos.
     *
     * La unique constraint venta + tipo evita duplicados si el cierre
     * del lote se ejecuta accidentalmente más de una vez.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "venta_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_penalizaciones_venta"
            )
    )
    private VentaConcretada venta;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo",
            nullable = false,
            length = 50
    )
    private TipoPenalizacion tipo;

    @Column(
            name = "importe",
            nullable = false,
            precision = 18,
            scale = 2
    )
    private BigDecimal importe;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "moneda",
            nullable = false,
            length = 20
    )
    private Moneda moneda;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado",
            nullable = false,
            length = 30
    )
    private EstadoPenalizacion estado;

    @Column(
            name = "fecha_generacion",
            nullable = false
    )
    private LocalDateTime fechaGeneracion;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    protected Penalizacion() {
    }

    public Penalizacion(Cliente cliente,
                        VentaConcretada venta,
                        BigDecimal importe,
                        Moneda moneda,
                        TipoPenalizacion tipo) {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio"
            );
        }

        if (venta == null) {
            throw new IllegalArgumentException(
                    "La venta es obligatoria"
            );
        }

        if (importe == null
                || importe.signum() <= 0) {

            throw new IllegalArgumentException(
                    "El importe de la penalización debe ser positivo"
            );
        }

        if (moneda == null) {
            throw new IllegalArgumentException(
                    "La moneda es obligatoria"
            );
        }

        if (tipo == null) {
            throw new IllegalArgumentException(
                    "El tipo de penalización es obligatorio"
            );
        }

        this.cliente = cliente;
        this.venta = venta;
        this.importe = importe;
        this.moneda = moneda;
        this.tipo = tipo;

        this.estado =
                EstadoPenalizacion.PENDIENTE;

        this.fechaGeneracion =
                LocalDateTime.now();
    }

    public Long getIdPenalizacion() {
        return idPenalizacion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public VentaConcretada getVenta() {
        return venta;
    }

    public TipoPenalizacion getTipo() {
        return tipo;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public EstadoPenalizacion getEstado() {
        return estado;
    }

    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public boolean estaPendiente() {
        return estado ==
                EstadoPenalizacion.PENDIENTE;
    }

    public void marcarPagada() {

        if (estado != EstadoPenalizacion.PENDIENTE) {
            throw new IllegalStateException(
                    "La penalización no está pendiente"
            );
        }

        this.estado =
                EstadoPenalizacion.PAGADA;

        this.fechaPago =
                LocalDateTime.now();
    }

    public void cancelar() {

        if (estado != EstadoPenalizacion.PENDIENTE) {
            throw new IllegalStateException(
                    "Solo una penalización pendiente puede cancelarse"
            );
        }

        this.estado =
                EstadoPenalizacion.CANCELADA;
    }
}