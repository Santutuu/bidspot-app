package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "itemscatalogo")
public class ItemCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long idItemCatalogo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "catalogo", nullable = false)
    private Catalogo catalogo;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto", nullable = false)
    private Item item;

    @Column(name = "preciobase", nullable = false)
    private float precioBase;

    @Column(name = "comision", nullable = false)
    private float comision;

    /*
     * Campo legacy.
     */
    @Column(name = "subastado")
    private String subastado = "no";

    /*
     * Extensión descriptiva para facilitar inspección y compatibilidad.
     */
    @Column(name = "estado_app")
    private String estadoApp;

    /*
     * Extensión normalizada del estado.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estadoitemcatalogo")
    private EstadoItemCatalogoEntity estado;

    /*
     * Extensión que referencia a la puja vigente de la tabla legacy pujos.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puja_actual_id", unique = true)
    private Puja pujaActual;

    @OneToMany(
            mappedBy = "itemCatalogo",
            fetch = FetchType.LAZY
    )
    private List<Puja> pujas =
            new ArrayList<>();

    public ItemCatalogo() {
    }

    public ItemCatalogo(
            Item item,
            float precioBase,
            float comision
    ) {
        this.item = item;
        this.precioBase = precioBase;
        this.comision = comision;
        this.subastado = "no";
    }

    public Long getIdItemCatalogo() {
        return idItemCatalogo;
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public Item getItem() {
        return item;
    }

    public float getPrecioBase() {
        return precioBase;
    }

    public float getComision() {
        return comision;
    }

    public EstadoItemCatalogo getEstado() {
        if (estado != null && estado.getNombre() != null) {
            return estado.getNombre();
        }

        if (estadoApp != null && !estadoApp.isBlank()) {
            try {
                return EstadoItemCatalogo.valueOf(
                        estadoApp.trim().toUpperCase()
                );
            } catch (IllegalArgumentException ignored) {
            }
        }

        if ("si".equalsIgnoreCase(subastado)) {
            return EstadoItemCatalogo.VENDIDO;
        }

        return EstadoItemCatalogo.PENDIENTE;
    }

    public EstadoItemCatalogoEntity getEstadoEntity() {
        return estado;
    }

    public String getEstadoApp() {
        return estadoApp;
    }

    public String getSubastado() {
        return subastado;
    }

    public Puja getPujaActual() {
        return pujaActual;
    }

    public List<Puja> getPujas() {
        return pujas;
    }

    public void setCatalogo(
            Catalogo catalogo
    ) {
        this.catalogo = catalogo;
    }

    public void setItem(
            Item item
    ) {
        this.item = item;
    }

    public void setPrecioBase(
            float precioBase
    ) {
        this.precioBase = precioBase;
    }

    public void setComision(
            float comision
    ) {
        this.comision = comision;
    }

    public void setPujaActual(
            Puja pujaActual
    ) {
        this.pujaActual = pujaActual;
    }

    public void setEstadoEntity(
            EstadoItemCatalogoEntity estado
    ) {
        this.estado = estado;

        if (estado == null || estado.getNombre() == null) {
            return;
        }

        sincronizarEstado(estado.getNombre());
    }

    private void sincronizarEstado(
            EstadoItemCatalogo estadoNuevo
    ) {
        this.estadoApp = estadoNuevo.name();

        switch (estadoNuevo) {
            case VENDIDO -> {
                this.subastado = "si";

                if (item != null) {
                    item.marcarComoVendido();
                }
            }

            case EN_REMATE -> {
                this.subastado = "no";

                if (item != null) {
                    item.setEstado(EstadoItem.EN_SUBASTA);
                }
            }

            case PENDIENTE,
                 SIN_OFERTAS,
                 CANCELADO -> this.subastado = "no";
        }
    }

    public void recibirPuja(
            Puja puja
    ) {
        if (puja == null || puja.getMonto() == null) {
            throw new IllegalArgumentException(
                    "La puja es obligatoria"
            );
        }

        if (!verificarPuja(puja.getMonto())) {
            throw new IllegalArgumentException(
                    "El monto de la puja no es válido"
            );
        }

        pujas.add(puja);
        this.pujaActual = puja;
    }

    public boolean verificarPuja(
            float monto
    ) {
        float mejorOferta = obtenerMejorOferta();

        return monto >= mejorOferta + calcularIncrementoMinimo()
                && monto <= mejorOferta + calcularIncrementoMaximo();
    }

    public float obtenerMejorOferta() {
        if (pujaActual != null
                && pujaActual.getMonto() != null) {
            return pujaActual.getMonto();
        }

        return precioBase;
    }

    public float calcularIncrementoMinimo() {
        return precioBase * 0.01f;
    }

    public float calcularIncrementoMaximo() {
        return precioBase * 0.20f;
    }

    public boolean tieneOfertas() {
        return pujaActual != null
                || (pujas != null && !pujas.isEmpty());
    }

    public Cliente obtenerClienteGanador() {
        if (pujaActual == null
                || pujaActual.getAsistente() == null) {
            return null;
        }

        return pujaActual
                .getAsistente()
                .getCliente();
    }

    /*
     * Compatibilidad temporal con código antiguo.
     */
    public Usuario obtenerGanador() {
        Cliente cliente = obtenerClienteGanador();

        if (cliente == null
                || cliente.getPersona() == null) {
            return null;
        }

        return cliente.getPersona().getUsuario();
    }
}