package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Subasta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSubasta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoSubasta estadoSubasta = EstadoSubasta.PROGRAMADA;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaUsuario categoriaMin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Moneda moneda;

    private String ubicacion;

    private String linkVivo;

    @ManyToOne
    @JoinColumn(name = "rematador_id")
    private Rematador rematador;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @OneToOne(mappedBy = "subasta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Catalogo catalogo;

    public Subasta() {
    }

    public Subasta(CategoriaUsuario categoriaMin,
                   Moneda moneda,
                   String ubicacion,
                   String linkVivo,
                   Rematador rematador,
                   LocalDateTime fechaInicio) {
        this.categoriaMin = categoriaMin;
        this.moneda = moneda;
        this.ubicacion = ubicacion;
        this.linkVivo = linkVivo;
        this.rematador = rematador;
        this.fechaInicio = fechaInicio;
        this.estadoSubasta = EstadoSubasta.PROGRAMADA;
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public EstadoSubasta getEstadoSubasta() {
        return estadoSubasta;
    }

    public CategoriaUsuario getCategoriaMin() {
        return categoriaMin;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getLinkVivo() {
        return linkVivo;
    }

    public Rematador getRematador() {
        return rematador;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(Catalogo catalogo) {
        this.catalogo = catalogo;
        catalogo.setSubasta(this);
    }


    public void iniciarSubasta() {
        this.estadoSubasta = EstadoSubasta.ACTIVA;
    }

    public void finalizarSubasta() {
        this.estadoSubasta = EstadoSubasta.FINALIZADA;
    }

    public void cancelarSubasta() {
        this.estadoSubasta = EstadoSubasta.CANCELADA;
    }
}