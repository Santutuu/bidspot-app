package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pujos")
public class Puja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long idPuja;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "asistente",
            nullable = false
    )
    private Asistente asistente;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "item",
            nullable = false
    )
    private ItemCatalogo itemCatalogo;

    @Column(name = "importe", nullable = false)
    private BigDecimal monto;

    /*
     * Extensión del modelo legacy.
     */
    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    /*
     * Extensión del modelo legacy.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_puja")
    private EstadoPuja estado =
            EstadoPuja.REGISTRADA;

    /*
     * Campo original legacy.
     */
    @Column(name = "ganador")
    private String ganador = "no";

    public Puja() {
    }

    public Puja(
            Asistente asistente,
            ItemCatalogo itemCatalogo,
            Float monto
    ) {
        this.asistente = asistente;
        this.itemCatalogo = itemCatalogo;
        this.monto = monto != null
                ? BigDecimal.valueOf(monto)
                : null;

        this.fechaHora = LocalDateTime.now();
        this.estado = EstadoPuja.REGISTRADA;
        this.ganador = "no";
    }

    @PrePersist
    private void prePersist() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }

        if (estado == null) {
            estado = EstadoPuja.REGISTRADA;
        }

        if (ganador == null) {
            ganador = "no";
        }
    }

    public Long getIdPuja() {
        return idPuja;
    }

    public Usuario getUsuario() {
        if (asistente == null
                || asistente.getCliente() == null
                || asistente.getCliente().getPersona() == null) {
            return null;
        }

        return asistente
                .getCliente()
                .getPersona()
                .getUsuario();
    }

    public Subasta getSubasta() {
        return asistente == null
                ? null
                : asistente.getSubasta();
    }

    public Asistente getAsistente() {
        return asistente;
    }

    public ItemCatalogo getItemCatalogo() {
        return itemCatalogo;
    }

    public Float getMonto() {
        return monto == null
                ? null
                : monto.floatValue();
    }

    public BigDecimal getMontoDecimal() {
        return monto;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public EstadoPuja getEstado() {
        return estado;
    }

    public String getGanador() {
        return ganador;
    }

    public void setItemCatalogo(
            ItemCatalogo itemCatalogo
    ) {
        this.itemCatalogo = itemCatalogo;
    }

    public void setAsistente(
            Asistente asistente
    ) {
        this.asistente = asistente;
    }

    public void marcarSuperada() {
        this.estado = EstadoPuja.SUPERADA;
        this.ganador = "no";
    }

    public void marcarRegistrada() {
        this.estado = EstadoPuja.REGISTRADA;
        this.ganador = "no";
    }

    public void marcarGanadora() {
        this.estado = EstadoPuja.GANADORA;
        this.ganador = "si";
    }

    public void marcarRechazada() {
        this.estado = EstadoPuja.RECHAZADA;
        this.ganador = "no";
    }
}