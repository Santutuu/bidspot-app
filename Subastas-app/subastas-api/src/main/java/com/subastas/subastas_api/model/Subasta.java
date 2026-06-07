package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
public class Subasta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSubasta;

    @ManyToOne
    @JoinColumn(name = "duenio_id")
    private Usuario duenio;

    @OneToOne
    private Item item;

    @ManyToOne
    private Rematador rematador;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaInicio;

    @Temporal(TemporalType.TIMESTAMP)
    private Date horaInicio;

    private float precioInicial;

    @Enumerated(EnumType.STRING)
    private Moneda moneda;

    @Enumerated(EnumType.STRING)
    private CategoriaUsuario categoriaMin;

    @OneToOne
    private Puja pujaActual;

    @OneToMany(mappedBy = "subasta", cascade = CascadeType.ALL)
    private List<Puja> pujas;

    @Enumerated(EnumType.STRING)
    private EstadoSubasta estadoSubasta;

    public Subasta() {
    }

    public Subasta(Usuario duenio,
                   Item item,
                   Rematador rematador,
                   Date fechaInicio,
                   Date horaInicio,
                   float precioInicial,
                   Moneda moneda,
                   CategoriaUsuario categoriaMin,
                   Puja pujaActual,
                   List<Puja> pujas,
                   EstadoSubasta estadoSubasta) {

        this.duenio = duenio;
        this.item = item;
        this.rematador = rematador;
        this.fechaInicio = fechaInicio;
        this.horaInicio = horaInicio;
        this.precioInicial = precioInicial;
        this.moneda = moneda;
        this.categoriaMin = categoriaMin;
        this.pujaActual = pujaActual;
        this.pujas = pujas;
        this.estadoSubasta = estadoSubasta;
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public Usuario getDuenio() {
        return duenio;
    }

    public void setDuenio(Usuario duenio) {
        this.duenio = duenio;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Rematador getRematador() {
        return rematador;
    }

    public void setRematador(Rematador rematador) {
        this.rematador = rematador;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public float getPrecioInicial() {
        return precioInicial;
    }

    public void setPrecioInicial(float precioInicial) {
        this.precioInicial = precioInicial;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public CategoriaUsuario getCategoriaMin() {
        return categoriaMin;
    }

    public void setCategoriaMin(CategoriaUsuario categoriaMin) {
        this.categoriaMin = categoriaMin;
    }

    public Puja getPujaActual() {
        return pujaActual;
    }

    public void setPujaActual(Puja pujaActual) {
        this.pujaActual = pujaActual;
    }

    public List<Puja> getPujas() {
        return pujas;
    }

    public void setPujas(List<Puja> pujas) {
        this.pujas = pujas;
    }

    public EstadoSubasta getEstadoSubasta() {
        return estadoSubasta;
    }

    public void setEstadoSubasta(EstadoSubasta estadoSubasta) {
        this.estadoSubasta = estadoSubasta;
    }
}