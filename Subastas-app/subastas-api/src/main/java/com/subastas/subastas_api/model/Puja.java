package com.subastas.subastas_api.model;

import jakarta.persistence.*;
import org.springframework.cglib.core.Local;


import java.time.LocalDateTime;
import java.util.Date;


@Entity
public class Puja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPuja;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "subasta_id")
    private Subasta subasta;

    private LocalDateTime fechaHora;

    private float monto;

    public Puja() {}

    public Puja(Usuario usuario, Subasta subasta, LocalDateTime fechaHora, float monto) {
        this.usuario = usuario;
        this.subasta = subasta;
        this.fechaHora = fechaHora;
        this.monto = monto;
    }

    public Long getIdPuja() { return idPuja; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Subasta getSubasta() { return subasta; }
    public void setSubasta(Subasta subasta) { this.subasta = subasta; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFecha(Date fecha) { this.fechaHora = fechaHora; }

    public float getMonto() { return monto; }
    public void setMonto(float monto) { this.monto = monto; }
}