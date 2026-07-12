package com.subastas.subastas_api.model;

import com.subastas.subastas_api.converter.CategoriaUsuarioConverter;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @Column(name = "identificador")
    private Long identificador;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "identificador")
    private Persona persona;

    @Column(name = "numeropais")
    private Integer numeroPais;

    @Column(name = "admitido")
    private String admitido;

    @Convert(converter = CategoriaUsuarioConverter.class)
    @Column(name = "categoria")
    private CategoriaUsuario categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "verificador", nullable = false)
    private Empleado verificador;

    @OneToOne(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "cuenta_id", unique = true)
    private CuentaBanco cuenta;

    @OneToMany(
            mappedBy = "cliente",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MedioDePago> mediosDePago = new ArrayList<>();

    public Cliente() {
    }

    public Cliente(Persona persona,
                   CategoriaUsuario categoria,
                   Empleado verificador) {
        this.persona = persona;
        this.admitido = "si";
        this.categoria = categoria;
        this.verificador = verificador;
    }

    public Cliente(Persona persona,
                   Integer numeroPais,
                   CategoriaUsuario categoria,
                   Empleado verificador) {
        this.persona = persona;
        this.numeroPais = numeroPais;
        this.admitido = "si";
        this.categoria = categoria;
        this.verificador = verificador;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public Persona getPersona() {
        return persona;
    }

    public Integer getNumeroPais() {
        return numeroPais;
    }

    public String getAdmitido() {
        return admitido;
    }

    public CategoriaUsuario getCategoria() {
        return categoria;
    }

    public Empleado getVerificador() {
        return verificador;
    }

    public CuentaBanco getCuenta() {
        return cuenta;
    }

    public List<MedioDePago> getMediosDePago() {
        return mediosDePago;
    }

    public boolean estaAdmitido() {
        return "si".equalsIgnoreCase(admitido);
    }

    public void aprobar(CategoriaUsuario categoria,
                        Empleado verificador) {
        this.admitido = "si";
        this.categoria = categoria;
        this.verificador = verificador;
    }

    public void aprobar(CategoriaUsuario categoria,
                        Integer numeroPais,
                        Empleado verificador) {
        this.admitido = "si";
        this.categoria = categoria;
        this.numeroPais = numeroPais;
        this.verificador = verificador;
    }

    public void rechazar() {
        this.admitido = "no";
    }

    public void setCuenta(CuentaBanco cuenta) {
        this.cuenta = cuenta;
    }

    public void agregarMedioDePago(MedioDePago medioDePago) {
        if (medioDePago == null) {
            return;
        }

        mediosDePago.add(medioDePago);
        medioDePago.setCliente(this);
    }

    public void eliminarMedioDePago(MedioDePago medioDePago) {
        if (medioDePago == null) {
            return;
        }

        mediosDePago.remove(medioDePago);
        medioDePago.setCliente(null);
    }
}