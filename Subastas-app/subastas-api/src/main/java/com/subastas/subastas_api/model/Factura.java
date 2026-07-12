package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFactura;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEmision;

    private float montoPuja;

    private float comision;

    private float costoEnvio;

    private float total;

    private String moneda;

    /*
     * El titular/comprador de la factura es Cliente.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comprador_id", nullable = false)
    private Cliente comprador;

    @OneToOne
    private Item item;

    public Factura() {
    }

    public Factura(Date fechaEmision,
                   float montoPuja,
                   float comision,
                   float costoEnvio,
                   float total,
                   String moneda,
                   Cliente comprador,
                   Item item) {
        this.fechaEmision = fechaEmision;
        this.montoPuja = montoPuja;
        this.comision = comision;
        this.costoEnvio = costoEnvio;
        this.total = total;
        this.moneda = moneda;
        this.comprador = comprador;
        this.item = item;
    }

    public Long getIdFactura() {
        return idFactura;
    }

    public Date getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Date fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public float getMontoPuja() {
        return montoPuja;
    }

    public void setMontoPuja(float montoPuja) {
        this.montoPuja = montoPuja;
    }

    public float getComision() {
        return comision;
    }

    public void setComision(float comision) {
        this.comision = comision;
    }

    public float getCostoEnvio() {
        return costoEnvio;
    }

    public void setCostoEnvio(float costoEnvio) {
        this.costoEnvio = costoEnvio;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public Cliente getComprador() {
        return comprador;
    }

    public void setComprador(Cliente comprador) {
        this.comprador = comprador;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}