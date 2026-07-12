package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class VentaConcretada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    /*
     * El comprador de negocio es Cliente.
     *
     * La cuenta Usuario queda solamente como autenticación.
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "comprador_id", nullable = false)
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVenta estado = EstadoVenta.PENDIENTE_PAGO;

    @Enumerated(EnumType.STRING)
    private TipoEntrega tipoEntrega;

    private String direccionEntrega;

    private String ubicacionRetiro;

    private Float montoPuja;

    private Float comision;

    private Float costoEnvio;

    private Float total;

    private LocalDateTime fechaVenta;

    private LocalDateTime fechaPagoConfirmado;

    public VentaConcretada() {
    }

    public VentaConcretada(Cliente comprador,
                           ItemCatalogo itemCatalogo,
                           Puja pujaGanadora,
                           Float montoPuja,
                           Float comision,
                           Float costoEnvio,
                           Float total,
                           String ubicacionRetiro) {
        this.comprador = comprador;
        this.itemCatalogo = itemCatalogo;
        this.pujaGanadora = pujaGanadora;
        this.montoPuja = montoPuja;
        this.comision = comision;
        this.costoEnvio = costoEnvio;
        this.total = total;
        this.ubicacionRetiro = ubicacionRetiro;
        this.estado = EstadoVenta.PENDIENTE_PAGO;
        this.fechaVenta = LocalDateTime.now();
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

    public void configurarEntregaDomicilio(String direccionEntrega,
                                           Float costoEnvio) {
        this.tipoEntrega = TipoEntrega.DOMICILIO;
        this.direccionEntrega = direccionEntrega;
        this.costoEnvio = costoEnvio;
        recalcularTotal();
    }

    public void configurarRetiro(String ubicacionRetiro) {
        this.tipoEntrega = TipoEntrega.RETIRO;
        this.ubicacionRetiro = ubicacionRetiro;
        this.direccionEntrega = null;
        this.costoEnvio = 0f;
        recalcularTotal();
    }

    public void seleccionarMedioPago(MedioDePago medioPago) {
        this.medioPago = medioPago;
    }

    public void confirmarPago() {
        this.estado = EstadoVenta.PAGO_CONFIRMADO;
        this.fechaPagoConfirmado = LocalDateTime.now();
    }

    public void marcarPreparandoEnvio() {
        this.estado = EstadoVenta.PREPARANDO_ENVIO;
    }

    public void marcarEnviado() {
        this.estado = EstadoVenta.ENVIADO;
    }

    public void marcarEnCamino() {
        this.estado = EstadoVenta.EN_CAMINO;
    }

    public void marcarEntregado() {
        this.estado = EstadoVenta.ENTREGADO;
    }

    public void marcarMultada() {
        this.estado = EstadoVenta.MULTADA;
    }

    public void marcarIncumplida() {
        this.estado = EstadoVenta.INCUMPLIDA;
    }

    public void cancelar() {
        this.estado = EstadoVenta.CANCELADA;
    }

    private void recalcularTotal() {
        float monto = montoPuja != null ? montoPuja : 0f;
        float com = comision != null ? comision : 0f;
        float envio = costoEnvio != null ? costoEnvio : 0f;

        this.total = monto + com + envio;
    }
}