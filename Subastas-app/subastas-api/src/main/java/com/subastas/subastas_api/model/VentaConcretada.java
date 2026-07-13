package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class VentaConcretada {

    private static final long HORAS_LIMITE_PAGO = 72L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "comprador_id",
            nullable = false
    )
    private Cliente comprador;

    @OneToOne(optional = false)
    @JoinColumn(name = "item_catalogo_id")
    private ItemCatalogo itemCatalogo;

    @OneToOne
    @JoinColumn(name = "puja_ganadora_id")
    private Puja pujaGanadora;

    @ManyToOne
    @JoinColumn(name = "medio_pago_id")
    private MedioDePago medioPago;

    @OneToOne
    @JoinColumn(
            name = "factura_id_factura",
            unique = true
    )
    private Factura factura;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVenta estado =
            EstadoVenta.PENDIENTE_PAGO;

    @Enumerated(EnumType.STRING)
    private TipoEntrega tipoEntrega;

    private String direccionEntrega;

    private String ubicacionRetiro;

    private Float montoPuja;

    private Float comision;

    private Float costoEnvio;

    private Float total;

    @Column(nullable = false)
    private LocalDateTime fechaVenta;

    private LocalDateTime fechaPagoConfirmado;

    @Column(
            name = "fecha_limite_pago",
            nullable = false
    )
    private LocalDateTime fechaLimitePago;

    @Column(name = "fecha_incumplimiento")
    private LocalDateTime fechaIncumplimiento;

    public VentaConcretada() {
    }

    public VentaConcretada(
            Cliente comprador,
            ItemCatalogo itemCatalogo,
            Puja pujaGanadora,
            Float montoPuja,
            Float comision,
            Float costoEnvio,
            Float total,
            String ubicacionRetiro
    ) {
        this.comprador = comprador;
        this.itemCatalogo = itemCatalogo;
        this.pujaGanadora = pujaGanadora;
        this.montoPuja = montoPuja;
        this.comision = comision;
        this.costoEnvio = costoEnvio;
        this.total = total;
        this.ubicacionRetiro = ubicacionRetiro;

        this.estado =
                EstadoVenta.PENDIENTE_PAGO;

        this.fechaVenta =
                LocalDateTime.now();

        this.fechaLimitePago =
                fechaVenta.plusHours(
                        HORAS_LIMITE_PAGO
                );
    }

    public Long getIdVenta() {
        return idVenta;
    }

    public Cliente getComprador() {
        return comprador;
    }

    public ItemCatalogo getItemCatalogo() {
        return itemCatalogo;
    }

    public Puja getPujaGanadora() {
        return pujaGanadora;
    }

    public MedioDePago getMedioPago() {
        return medioPago;
    }

    public Factura getFactura() {
        return factura;
    }

    public EstadoVenta getEstado() {
        return estado;
    }

    public TipoEntrega getTipoEntrega() {
        return tipoEntrega;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public String getUbicacionRetiro() {
        return ubicacionRetiro;
    }

    public Float getMontoPuja() {
        return montoPuja;
    }

    public Float getComision() {
        return comision;
    }

    public Float getCostoEnvio() {
        return costoEnvio;
    }

    public Float getTotal() {
        return total;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }

    public LocalDateTime getFechaPagoConfirmado() {
        return fechaPagoConfirmado;
    }

    public LocalDateTime getFechaLimitePago() {
        return fechaLimitePago;
    }

    public LocalDateTime getFechaIncumplimiento() {
        return fechaIncumplimiento;
    }

    public void configurarEntregaDomicilio(
            String direccionEntrega,
            Float costoEnvio
    ) {
        validarPendientePago();

        if (direccionEntrega == null
                || direccionEntrega.isBlank()) {

            throw new IllegalArgumentException(
                    "La dirección de entrega es obligatoria"
            );
        }

        if (costoEnvio == null
                || costoEnvio < 0f) {

            throw new IllegalArgumentException(
                    "El costo de envío no puede ser negativo"
            );
        }

        this.tipoEntrega =
                TipoEntrega.DOMICILIO;

        this.direccionEntrega =
                direccionEntrega;

        this.costoEnvio =
                costoEnvio;

        recalcularTotal();
    }

    public void configurarRetiro(
            String ubicacionRetiro
    ) {
        validarPendientePago();

        if (ubicacionRetiro == null
                || ubicacionRetiro.isBlank()) {

            throw new IllegalArgumentException(
                    "La ubicación de retiro es obligatoria"
            );
        }

        this.tipoEntrega =
                TipoEntrega.RETIRO;

        this.ubicacionRetiro =
                ubicacionRetiro;

        this.direccionEntrega =
                null;

        this.costoEnvio =
                0f;

        recalcularTotal();
    }

    public void seleccionarMedioPago(
            MedioDePago medioPago
    ) {
        validarPendientePago();

        if (medioPago == null) {
            throw new IllegalArgumentException(
                    "El medio de pago es obligatorio"
            );
        }

        this.medioPago =
                medioPago;
    }

    public void asociarFactura(
            Factura factura
    ) {
        if (factura == null) {
            throw new IllegalArgumentException(
                    "La factura es obligatoria"
            );
        }

        if (this.factura != null) {
            throw new IllegalStateException(
                    "La venta ya tiene una factura asociada"
            );
        }

        this.factura = factura;
    }

    public boolean estaVencida() {
        return estado
                == EstadoVenta.PENDIENTE_PAGO
                && fechaLimitePago != null
                && !LocalDateTime.now()
                .isBefore(fechaLimitePago);
    }

    public void confirmarPago() {
        validarPendientePago();

        if (estaVencida()) {
            throw new IllegalStateException(
                    "La compra superó el plazo de pago"
            );
        }

        if (tipoEntrega == null) {
            throw new IllegalStateException(
                    "Debe configurarse el tipo de entrega antes de confirmar el pago"
            );
        }

        if (medioPago == null) {
            throw new IllegalStateException(
                    "Debe seleccionarse un medio de pago antes de confirmar el pago"
            );
        }

        estado =
                EstadoVenta.PAGO_CONFIRMADO;

        fechaPagoConfirmado =
                LocalDateTime.now();
    }

    public void marcarIncumplida() {
        if (estado != EstadoVenta.PENDIENTE_PAGO) {
            throw new IllegalStateException(
                    "Solo una venta pendiente de pago puede marcarse como incumplida"
            );
        }

        estado =
                EstadoVenta.INCUMPLIDA;

        fechaIncumplimiento =
                LocalDateTime.now();
    }

    public void marcarPreparandoEnvio() {
        validarTipoEntrega(
                TipoEntrega.DOMICILIO
        );

        if (estado != EstadoVenta.PAGO_CONFIRMADO) {
            throw new IllegalStateException(
                    "La venta debe tener el pago confirmado para preparar el envío"
            );
        }

        estado =
                EstadoVenta.PREPARANDO_ENVIO;
    }

    public void marcarEnviado() {
        validarTipoEntrega(
                TipoEntrega.DOMICILIO
        );

        if (estado != EstadoVenta.PREPARANDO_ENVIO) {
            throw new IllegalStateException(
                    "La venta debe estar preparando el envío"
            );
        }

        estado =
                EstadoVenta.ENVIADO;
    }

    public void marcarEnCamino() {
        validarTipoEntrega(
                TipoEntrega.DOMICILIO
        );

        if (estado != EstadoVenta.ENVIADO) {
            throw new IllegalStateException(
                    "La venta debe estar enviada"
            );
        }

        estado =
                EstadoVenta.EN_CAMINO;
    }

    public void marcarPreparandoRetiro() {
        validarTipoEntrega(
                TipoEntrega.RETIRO
        );

        if (estado != EstadoVenta.PAGO_CONFIRMADO) {
            throw new IllegalStateException(
                    "La venta debe tener el pago confirmado"
            );
        }

        estado =
                EstadoVenta.PREPARANDO_RETIRO;
    }

    public void marcarListaParaRetirar() {
        validarTipoEntrega(
                TipoEntrega.RETIRO
        );

        if (estado != EstadoVenta.PREPARANDO_RETIRO) {
            throw new IllegalStateException(
                    "La venta debe estar preparando el retiro"
            );
        }

        estado =
                EstadoVenta.LISTO_PARA_RETIRAR;
    }

    public void marcarEntregado() {
        validarTipoEntrega(
                TipoEntrega.DOMICILIO
        );

        if (estado != EstadoVenta.EN_CAMINO) {
            throw new IllegalStateException(
                    "La venta debe estar en camino"
            );
        }

        estado =
                EstadoVenta.ENTREGADO;
    }

    public void marcarRetirado() {
        validarTipoEntrega(
                TipoEntrega.RETIRO
        );

        if (estado != EstadoVenta.LISTO_PARA_RETIRAR) {
            throw new IllegalStateException(
                    "La venta debe estar lista para retirar"
            );
        }

        estado =
                EstadoVenta.RETIRADO;
    }

    public void cancelar() {
        estado =
                EstadoVenta.CANCELADA;
    }

    private void validarPendientePago() {
        if (estado != EstadoVenta.PENDIENTE_PAGO) {
            throw new IllegalStateException(
                    "La venta ya no está pendiente de pago"
            );
        }
    }

    private void validarTipoEntrega(
            TipoEntrega tipoEsperado
    ) {
        if (tipoEntrega != tipoEsperado) {
            throw new IllegalStateException(
                    "La venta no utiliza el tipo de entrega requerido"
            );
        }
    }

    private void recalcularTotal() {
        float monto =
                montoPuja != null
                        ? montoPuja
                        : 0f;

        float com =
                comision != null
                        ? comision
                        : 0f;

        float envio =
                costoEnvio != null
                        ? costoEnvio
                        : 0f;

        total =
                monto
                        + com
                        + envio;
    }
}