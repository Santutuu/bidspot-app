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

    /*
     * Restricción comercial.
     *
     * Un Cliente suspendido puede seguir iniciando sesión y acceder
     * a sus compras, pero no puede realizar nuevas pujas.
     *
     * Esta suspensión es independiente del bloqueo técnico de Usuario.
     */
    @Column(
            name = "suspendido_para_pujar",
            nullable = false
    )
    private boolean suspendidoParaPujar = false;

    public Cliente() {
    }

    public Cliente(Persona persona,
                   CategoriaUsuario categoria,
                   Empleado verificador) {
        this.persona = persona;
        this.admitido = "si";
        this.categoria = categoria;
        this.verificador = verificador;
        this.suspendidoParaPujar = false;
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
        this.suspendidoParaPujar = false;
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

    public boolean isSuspendidoParaPujar() {
        return suspendidoParaPujar;
    }

    public boolean estaAdmitido() {
        return "si".equalsIgnoreCase(admitido);
    }

    /**
     * Un Cliente puede pujar únicamente cuando está admitido
     * y no posee una suspensión comercial activa.
     *
     * La validación de categoría, medios de pago, garantía, etc.
     * continúa siendo responsabilidad de PujaService.
     */
    public boolean puedePujar() {
        return estaAdmitido()
                && !suspendidoParaPujar;
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

    /**
     * Suspende comercialmente al Cliente.
     *
     * Se utilizará cuando se genere una Penalizacion pendiente
     * por falta de respaldo suficiente al cerrar un lote.
     */
    public void suspenderParaPujar() {
        this.suspendidoParaPujar = true;
    }

    /**
     * Habilita nuevamente al Cliente.
     *
     * El service encargado del pago de penalizaciones deberá comprobar
     * previamente que el Cliente no tenga otras penalizaciones pendientes.
     */
    public void habilitarParaPujar() {
        this.suspendidoParaPujar = false;
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