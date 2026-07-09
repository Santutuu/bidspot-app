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
     * Campo legacy.
     *
     * Se mantiene sincronizado con estadosubasta:
     *
     * ACTIVA      -> abierta
     * PROGRAMADA  -> programada
     * FINALIZADA  -> cerrada
     * CANCELADA   -> cancelada
     */
    @Column(name = "estado")
    private String estadoLegacy;

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

    /*
     * Método principal para asignar una entidad de estado persistente.
     *
     * Este es el método que deberían utilizar los services que obtienen
     * EstadoSubastaEntity desde EstadoSubastaRepository.
     */
    public void setEstado(EstadoSubastaEntity estado) {
        this.estado = estado;

        if (estado == null || estado.getNombre() == null) {
            return;
        }

        this.estadoLegacy = convertirEstadoLegacy(estado.getNombre());
    }

    public String getEstadoLegacy() {
        return estadoLegacy;
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
     * Se conserva temporalmente por compatibilidad con services existentes.
     *
     * ATENCIÓN:
     * EstadoSubastaEntity es una entidad persistente.
     *
     * Crear una instancia nueva acá puede provocar TransientObjectException
     * si posteriormente Hibernate intenta persistir la Subasta.
     *
     * Los services refactorizados deberían utilizar:
     *
     * EstadoSubastaRepository.findByNombre(...)
     * subasta.setEstado(estadoEntity)
     */
    public void setEstadoSubasta(EstadoSubasta estadoSubasta) {

        this.estado = new EstadoSubastaEntity(estadoSubasta);
        this.estadoLegacy = convertirEstadoLegacy(estadoSubasta);
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

    private String convertirEstadoLegacy(EstadoSubasta estadoSubasta) {

        if (estadoSubasta == null) {
            return null;
        }

        return switch (estadoSubasta) {
            case ACTIVA -> "abierta";
            case PROGRAMADA -> "programada";
            case FINALIZADA -> "carrada";
            case CANCELADA -> "cancelada";
        };
    }
}