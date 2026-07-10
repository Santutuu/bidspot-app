package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "persona_id", nullable = false, unique = true)
    private Persona persona;

    /**
     * Vincula la cuenta técnica de autenticación con el cliente
     * de negocio perteneciente al modelo legacy.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_legacy_id")
    private Cliente clienteLegacy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.USER;

    /**
     * Se conserva temporalmente para compatibilidad con el flujo
     * moderno de registro y con los datos existentes.
     *
     * La admisión de negocio debe consultarse principalmente
     * desde Cliente.admitido.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuario estado = EstadoUsuario.PENDIENTE_VALIDACION;

    /**
     * Se conserva temporalmente como fallback.
     *
     * La categoría de negocio debe obtenerse principalmente
     * desde Cliente.categoria.
     */
    @Enumerated(EnumType.STRING)
    private CategoriaUsuario categoria;

    @Column
    private String password;

    @ManyToMany
    @JoinTable(
            name = "usuario_subasta_guardada",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "subasta_id")
    )
    private List<Subasta> guardadas = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cuenta_id")
    private CuentaBanco cuenta;

    @OneToMany(
            mappedBy = "usuario",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<MedioDePago> mediosDePago = new ArrayList<>();

    public Usuario() {
    }

    public Usuario(Persona persona,
                   String password,
                   Rol rol,
                   EstadoUsuario estado) {
        this.persona = persona;
        this.password = password;
        this.rol = rol;
        this.estado = estado;
    }

    public boolean tieneClaveGenerada() {
        return password != null && !password.isBlank();
    }

    public void agregarMedioDePago(MedioDePago medioDePago) {
        mediosDePago.add(medioDePago);
        medioDePago.setUsuario(this);
    }

    public void eliminarMedioDePago(MedioDePago medioDePago) {
        mediosDePago.remove(medioDePago);
        medioDePago.setUsuario(null);
    }

    public void guardarSubasta(Subasta subasta) {
        if (!guardadas.contains(subasta)) {
            guardadas.add(subasta);
        }
    }

    public void eliminarSubasta(Subasta subasta) {
        guardadas.remove(subasta);
    }

    public boolean tieneSubastaGuardada(Subasta subasta) {
        return guardadas.contains(subasta);
    }

    /**
     * Fuente de verdad progresiva para la categoría.
     *
     * Si ya existe Cliente, usa Cliente.categoria.
     * Si todavía no existe, mantiene compatibilidad con Usuario.categoria.
     */
    public CategoriaUsuario getCategoriaNegocio() {
        if (clienteLegacy != null && clienteLegacy.getCategoria() != null) {
            return clienteLegacy.getCategoria();
        }

        return categoria;
    }

    /**
     * Un usuario está validado como cliente cuando existe su entidad
     * Cliente y la empresa lo marcó como admitido.
     */
    public boolean estaValidadoComoCliente() {
        return clienteLegacy != null && clienteLegacy.estaAdmitido();
    }

    /**
     * Estado efectivo para exponer al frontend durante la transición.
     *
     * BLOQUEADO y RECHAZADO conservan prioridad.
     * Si existe un cliente admitido, se considera VALIDADO.
     */
    public EstadoUsuario getEstadoEfectivo() {
        if (estado == EstadoUsuario.BLOQUEADO
                || estado == EstadoUsuario.RECHAZADO) {
            return estado;
        }

        if (estaValidadoComoCliente()) {
            return EstadoUsuario.VALIDADO;
        }

        return estado;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Persona getPersona() {
        return persona;
    }

    public Cliente getClienteLegacy() {
        return clienteLegacy;
    }

    public String getPassword() {
        return password;
    }

    public Rol getRol() {
        return rol;
    }

    public EstadoUsuario getEstado() {
        return estado;
    }

    public CategoriaUsuario getCategoria() {
        return categoria;
    }

    public CuentaBanco getCuenta() {
        return cuenta;
    }

    public List<MedioDePago> getMediosDePago() {
        return mediosDePago;
    }

    public List<Subasta> getGuardadas() {
        return guardadas;
    }

    public void setClienteLegacy(Cliente clienteLegacy) {
        this.clienteLegacy = clienteLegacy;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }

    public void setCategoria(CategoriaUsuario categoria) {
        this.categoria = categoria;
    }

    public void setCuenta(CuentaBanco cuenta) {
        this.cuenta = cuenta;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}