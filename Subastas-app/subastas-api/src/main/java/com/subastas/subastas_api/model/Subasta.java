package com.subastas.subastas_api.model;

import com.subastas.subastas_api.converter.CategoriaUsuarioConverter;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "subastas")
public class Subasta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long idSubasta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estadosubasta", nullable = false)
    private EstadoSubastaEntity estado;

    /*
     * En la base legacy las categorías están guardadas en minúsculas:
     * comun, especial, plata, oro, platino.
     *
     * El converter transforma esos valores a CategoriaUsuario.
     */
    @Convert(converter = CategoriaUsuarioConverter.class)
    @Column(name = "categoria", nullable = false)
    private CategoriaUsuario categoriaMin;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda", nullable = false)
    private Moneda moneda;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "linkvivo")
    private String linkVivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subastador")
    private Rematador rematador;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @OneToOne(
            mappedBy = "subasta",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
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

        setFechaInicio(fechaInicio);
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public EstadoSubasta getEstadoSubasta() {
        return estado == null
                ? null
                : estado.getNombre();
    }

    public EstadoSubastaEntity getEstado() {
        return estado;
    }

    public void setEstado(EstadoSubastaEntity estado) {
        this.estado = estado;
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

        if (fecha == null || hora == null) {
            return null;
        }

        return LocalDateTime.of(fecha, hora);
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {

        if (fechaInicio == null) {
            this.fecha = null;
            this.hora = null;
            return;
        }

        this.fecha = fechaInicio.toLocalDate();
        this.hora = fechaInicio.toLocalTime();
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public void setCatalogo(Catalogo catalogo) {

        this.catalogo = catalogo;

        if (catalogo != null && catalogo.getSubasta() != this) {
            catalogo.setSubasta(this);
        }
    }

    /*
     * Este método se conserva por compatibilidad con services actuales.
     *
     * IMPORTANTE:
     * EstadoSubastaEntity es una entidad persistente.
     * Crear una instancia nueva acá NO significa que ya exista en DB.
     *
     * Más adelante adaptaremos las transiciones de estado para obtener
     * EstadoSubastaEntity desde EstadoSubastaRepository.
     */
    public void setEstadoSubasta(EstadoSubasta estadoSubasta) {
        this.estado = new EstadoSubastaEntity(estadoSubasta);
    }

    public void iniciarSubasta() {
        setEstadoSubasta(EstadoSubasta.ACTIVA);
    }

    public void finalizarSubasta() {
        setEstadoSubasta(EstadoSubasta.FINALIZADA);
    }

    public void cancelarSubasta() {
        setEstadoSubasta(EstadoSubasta.CANCELADA);
    }
}