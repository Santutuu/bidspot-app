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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuario estado = EstadoUsuario.PENDIENTE_VALIDACION;

    @Enumerated(EnumType.STRING)
    private CategoriaUsuario categoria;

    @Column(nullable = true)
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

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedioDePago> mediosDePago = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<Puja> pujas = new ArrayList<>();

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
        guardadas.add(subasta);
    }

    public void eliminarSubasta(Subasta subasta) {
        guardadas.remove(subasta);
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public Persona getPersona() {
        return persona;
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

    public List<Puja> getPujas() {
        return pujas;
    }

    public List<Subasta> getGuardadas() {
        return guardadas;
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