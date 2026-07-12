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

    /*
     * Extensión del modelo legacy.
     *
     * Contiene el estado detallado utilizado por la aplicación:
     * PROGRAMADA, ACTIVA, FINALIZADA o CANCELADA.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estadosubasta")
    private EstadoSubastaEntity estado;

    /*
     * Campo original legacy.
     *
     * El CHECK de la base solamente admite:
     * abierta / carrada.
     */
    @Column(name = "estado")
    private String estadoLegacy;

    @Convert(converter = CategoriaUsuarioConverter.class)
    @Column(name = "categoria")
    private CategoriaUsuario categoriaMin;

    /*
     * Extensión necesaria para la aplicación.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "moneda")
    private Moneda moneda;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Column(name = "linkvivo")
    private String linkVivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subastador")
    private Rematador rematador;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora", nullable = false)
    private LocalTime hora;

    @Column(name = "capacidadasistentes")
    private Integer capacidadAsistentes;

    @Column(name = "tienedeposito")
    private String tieneDeposito;

    @Column(name = "seguridadpropia")
    private String seguridadPropia;

    @OneToOne(
            mappedBy = "subasta",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Catalogo catalogo;

    public Subasta() {
    }

    public Subasta(
            CategoriaUsuario categoriaMin,
            Moneda moneda,
            String ubicacion,
            String linkVivo,
            Rematador rematador,
            LocalDateTime fechaInicio
    ) {
        this.categoriaMin = categoriaMin;
        this.moneda = moneda;
        this.ubicacion = ubicacion;
        this.linkVivo = linkVivo;
        this.rematador = rematador;

        setFechaInicio(fechaInicio);
    }

    @PrePersist
    @PreUpdate
    private void sincronizarEstadoLegacy() {
        if (estado != null && estado.getNombre() != null) {
            this.estadoLegacy =
                    convertirEstadoLegacy(estado.getNombre());
        }
    }

    public Long getIdSubasta() {
        return idSubasta;
    }

    public EstadoSubasta getEstadoSubasta() {
        if (estado != null && estado.getNombre() != null) {
            return estado.getNombre();
        }

        /*
         * Fallback para filas legacy que todavía no tienen
         * estadosubasta configurado.
         */
        if ("abierta".equalsIgnoreCase(estadoLegacy)) {
            return EstadoSubasta.ACTIVA;
        }

        if ("carrada".equalsIgnoreCase(estadoLegacy)
                || "cerrada".equalsIgnoreCase(estadoLegacy)) {
            return EstadoSubasta.FINALIZADA;
        }

        return null;
    }

    public EstadoSubastaEntity getEstado() {
        return estado;
    }

    public void setEstado(
            EstadoSubastaEntity estado
    ) {
        this.estado = estado;

        if (estado != null && estado.getNombre() != null) {
            this.estadoLegacy =
                    convertirEstadoLegacy(estado.getNombre());
        }
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

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public Integer getCapacidadAsistentes() {
        return capacidadAsistentes;
    }

    public String getTieneDeposito() {
        return tieneDeposito;
    }

    public String getSeguridadPropia() {
        return seguridadPropia;
    }

    public LocalDateTime getFechaInicio() {
        if (fecha == null || hora == null) {
            return null;
        }

        return LocalDateTime.of(fecha, hora);
    }

    public void setFechaInicio(
            LocalDateTime fechaInicio
    ) {
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

    public void setCatalogo(
            Catalogo catalogo
    ) {
        this.catalogo = catalogo;

        if (catalogo != null
                && catalogo.getSubasta() != this) {
            catalogo.setSubasta(this);
        }
    }

    public void setCategoriaMin(
            CategoriaUsuario categoriaMin
    ) {
        this.categoriaMin = categoriaMin;
    }

    public void setMoneda(
            Moneda moneda
    ) {
        this.moneda = moneda;
    }

    public void setUbicacion(
            String ubicacion
    ) {
        this.ubicacion = ubicacion;
    }

    public void setLinkVivo(
            String linkVivo
    ) {
        this.linkVivo = linkVivo;
    }

    public void setRematador(
            Rematador rematador
    ) {
        this.rematador = rematador;
    }

    public void setCapacidadAsistentes(
            Integer capacidadAsistentes
    ) {
        this.capacidadAsistentes = capacidadAsistentes;
    }

    public void setTieneDeposito(
            String tieneDeposito
    ) {
        this.tieneDeposito = tieneDeposito;
    }

    public void setSeguridadPropia(
            String seguridadPropia
    ) {
        this.seguridadPropia = seguridadPropia;
    }

    private String convertirEstadoLegacy(
            EstadoSubasta estadoSubasta
    ) {
        if (estadoSubasta == null) {
            return estadoLegacy;
        }

        return switch (estadoSubasta) {
            case ACTIVA -> "abierta";

            /*
             * El legacy solamente distingue abierta/carrada.
             * El detalle real se conserva en estadosubasta.
             */
            case PROGRAMADA,
                 FINALIZADA,
                 CANCELADA -> "carrada";
        };
    }
}