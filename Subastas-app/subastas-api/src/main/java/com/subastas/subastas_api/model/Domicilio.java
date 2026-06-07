
package com.subastas.subastas_api.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


@Embeddable
@Setter
@Getter
public class Domicilio {

    private String provincia;
    private String ciudad;
    private String cp;
    private String direccion;

    public Domicilio() {}

    public Domicilio(String provincia, String ciudad, String cp, String direccion) {
        this.provincia = provincia;
        this.ciudad = ciudad;
        this.cp = cp;
        this.direccion = direccion;
    }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getCp() { return cp; }
    public void setCp(String cp) { this.cp = cp; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}