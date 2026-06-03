package com.subastas.subastas_api.model;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class TarjetaCredito extends MedioDePago {

    private int numero;
    private String nombre;
    private Date fechaVto;
    private int cvv;
    private boolean esPrincipal;

    public TarjetaCredito() {}

    public TarjetaCredito(Usuario usuario,
                          int numero,
                          String nombre,
                          Date fechaVto,
                          int cvv,
                          boolean esPrincipal) {

        super(usuario);

        this.numero = numero;
        this.nombre = nombre;
        this.fechaVto = fechaVto;
        this.cvv = cvv;
        this.esPrincipal = esPrincipal;
    }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Date getFechaVto() { return fechaVto; }
    public void setFechaVto(Date fechaVto) { this.fechaVto = fechaVto; }

    public int getCvv() { return cvv; }
    public void setCvv(int cvv) { this.cvv = cvv; }

    public boolean isEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(boolean esPrincipal) { this.esPrincipal = esPrincipal; }
}