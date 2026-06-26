package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class ItemCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemCatalogo;

    @ManyToOne
    @JoinColumn(name = "catalogo_id", nullable = false)
    private Catalogo catalogo;

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false, unique = true)
    private Item item;

    @Column(nullable = false)
    private float precioBase;

    @Column(nullable = false)
    private float comision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoItemCatalogo estado = EstadoItemCatalogo.PENDIENTE;

    @OneToOne
    @JoinColumn(name = "puja_actual_id")
    private Puja pujaActual;

    @OneToMany(mappedBy = "itemCatalogo", cascade = CascadeType.ALL)
    private List<Puja> pujas = new ArrayList<>();

    public ItemCatalogo() {
    }

    public ItemCatalogo(Item item, float precioBase, float comision) {
        this.item = item;
        this.precioBase = precioBase;
        this.comision = comision;
        this.estado = EstadoItemCatalogo.PENDIENTE;
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
        return estado;
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

    public void recibirPuja(Puja puja) {
        if (!verificarPuja(puja.getMonto())) {
            throw new IllegalArgumentException("El monto de la puja no es válido");
        }

        pujas.add(puja);
        puja.setItemCatalogo(this);
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
        if (pujaActual == null) {
            return null;
        }

        return pujaActual.getUsuario();
    }

    public void marcarEnRemate() {
        this.estado = EstadoItemCatalogo.EN_REMATE;
    }

    public void marcarComoVendido() {
        this.estado = EstadoItemCatalogo.VENDIDO;
        this.item.marcarComoVendido();
    }

    public void marcarSinOfertas() {
        this.estado = EstadoItemCatalogo.SIN_OFERTAS;
    }

    public Long getIdItemCatalogo() {
        return idItemCatalogo;
    }

}