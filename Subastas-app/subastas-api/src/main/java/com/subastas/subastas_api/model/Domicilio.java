package com.subastas.subastas_api.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class Domicilio {

    private String pais;
    private String provincia;
    private String ciudad;
    private String cp;
    private String direccion;

    public Domicilio() {
    }

    public Domicilio(String pais,
                     String provincia,
                     String ciudad,
                     String cp,
                     String direccion) {
        this.pais = pais;
        this.provincia = provincia;
        this.ciudad = ciudad;
        this.cp = cp;
        this.direccion = direccion;
    }

    public String getPais() {
        return pais;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getCp() {
        return cp;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}