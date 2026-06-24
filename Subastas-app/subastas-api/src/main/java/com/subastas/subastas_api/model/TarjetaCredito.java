package com.subastas.subastas_api.model;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class TarjetaCredito extends MedioDePago {

    private String numero;
    private String nombre;
    private String fechaVto;
    private String cvv;

    public TarjetaCredito() {}

    public TarjetaCredito(Usuario usuario,
                          String numero,
                          String nombre,
                          String fechaVto,
                          String cvv
                         ) {

        super(usuario);

        this.numero = numero;
        this.nombre = nombre;
        this.fechaVto = fechaVto;
        this.cvv = cvv;
    }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getFechaVto() { return fechaVto; }
    public void setFechaVto(String fechaVto) { this.fechaVto = fechaVto; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }


}