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
    private Integer idItemCatalogo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "catalogo", nullable = false)
    private Catalogo catalogo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto", nullable = false, unique = true)
    private Item item;

    @Column(name = "preciobase", nullable = false)
    private float precioBase;

    @Column(name = "comision", nullable = false)
    private float comision;

    @Column(name = "subastado")
    private String subastado = "no";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "estadoitemcatalogo", nullable = false)
    private EstadoItemCatalogoEntity estado;

    @Transient
    private Puja pujaActual;

    @Transient
    private List<Puja> pujas = new ArrayList<>();

    public ItemCatalogo() {
    }

    public ItemCatalogo(Item item, float precioBase, float comision) {
        this.item = item;
        this.precioBase = precioBase;
        this.comision = comision;
        this.subastado = "no";
    }

    public Integer getIdItemCatalogo() {
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
        return estado == null ? null : estado.getNombre();
    }

    public EstadoItemCatalogoEntity getEstadoEntity() {
        return estado;
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

    public void setCatalogo(Catalogo catalogo) {
        this.catalogo = catalogo;
    }

    public void setEstadoEntity(EstadoItemCatalogoEntity estado) {
        this.estado = estado;
    }

    public void setEstado(EstadoItemCatalogo estado) {
        this.estado = new EstadoItemCatalogoEntity(estado);

        if (estado == EstadoItemCatalogo.VENDIDO) {
            this.subastado = "si";
        }
    }

    public void recibirPuja(Puja puja) {
        if (!verificarPuja(puja.getMonto())) {
            throw new IllegalArgumentException("El monto de la puja no es válido");
        }

        pujas.add(puja);
        this.pujaActual = puja;
    }

    public boolean verificarPuja(float monto) {
        float mejorOferta = obtenerMejorOferta();

        return monto >= mejorOferta + calcularIncrementoMinimo()
                && monto <= mejorOferta + calcularIncrementoMaximo();
    }

    public float obtenerMejorOferta() {
        if (pujaActual != null) {
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
        return pujaActual != null;
    }

    public Usuario obtenerGanador() {
        return pujaActual == null ? null : pujaActual.getUsuario();
    }

    public void marcarEnRemate() {
        setEstado(EstadoItemCatalogo.EN_REMATE);
    }

    public void marcarComoVendido() {
        setEstado(EstadoItemCatalogo.VENDIDO);
        this.subastado = "si";

        if (this.item != null) {
            this.item.marcarComoVendido();
        }
    }

    public void marcarSinOfertas() {
        setEstado(EstadoItemCatalogo.SIN_OFERTAS);
        this.subastado = "no";
    }
}