package com.subastas.subastas_api.model;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class VentaConcretada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVenta;

    @ManyToOne
    private Usuario comprador;

    @Enumerated(EnumType.STRING)
    private EstadoVenta estado;

    @Temporal(TemporalType.DATE)
    private Date entregaEstimada;

    @OneToOne(cascade = CascadeType.ALL)
    private Factura factura;

    public VentaConcretada() {
    }

    public VentaConcretada(Usuario comprador,
                           EstadoVenta estado,
                           Date entregaEstimada,
                           Factura factura) {
        this.comprador = comprador;
        this.estado = estado;
        this.entregaEstimada = entregaEstimada;
        this.factura = factura;
    }

    // getters y setters
}