package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ParticipacionSubasta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParticipacion;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subasta_id")
    private Subasta subasta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoParticipacionSubasta estado = EstadoParticipacionSubasta.ACTIVA;

    private LocalDateTime fechaConexion;

    private LocalDateTime fechaSalida;

    public ParticipacionSubasta() {
    }

    public ParticipacionSubasta(Usuario usuario, Subasta subasta) {
        this.usuario = usuario;
        this.subasta = subasta;
        this.estado = EstadoParticipacionSubasta.ACTIVA;
        this.fechaConexion = LocalDateTime.now();
    }

    public Long getIdParticipacion() {
        return idParticipacion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Subasta getSubasta() {
        return subasta;
    }

    public EstadoParticipacionSubasta getEstado() {
        return estado;
    }

    public LocalDateTime getFechaConexion() {
        return fechaConexion;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public void finalizar() {
        this.estado = EstadoParticipacionSubasta.FINALIZADA;
        this.fechaSalida = LocalDateTime.now();
    }

    public void abandonar() {
        this.estado = EstadoParticipacionSubasta.ABANDONADA;
        this.fechaSalida = LocalDateTime.now();
    }
}