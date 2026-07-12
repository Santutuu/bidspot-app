package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @OneToOne(
            cascade = CascadeType.ALL,
            optional = false
    )
    @JoinColumn(
            name = "persona_id",
            nullable = false,
            unique = true
    )
    private Persona persona;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.USER;

    /*
     * Estado técnico de la cuenta.
     *
     * La validación comercial vive en Persona.estadoRegistro
     * y Cliente.admitido.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuario estado =
            EstadoUsuario.ACTIVO;

    @Column
    private String password;

    @ManyToMany
    @JoinTable(
            name = "usuario_subasta_guardada",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "subasta_id")
    )
    private List<Subasta> guardadas =
            new ArrayList<>();

    public Usuario() {
    }

    public Usuario(
            Persona persona,
            String password,
            Rol rol,
            EstadoUsuario estado
    ) {
        this.persona = persona;
        this.password = password;
        this.rol = rol;
        this.estado = estado != null
                ? estado
                : EstadoUsuario.ACTIVO;
    }

    public boolean tieneClaveGenerada() {
        return password != null
                && !password.isBlank();
    }

    public boolean estaBloqueado() {
        return estado == EstadoUsuario.BLOQUEADO;
    }

    public Cliente getCliente() {
        return persona != null
                ? persona.getCliente()
                : null;
    }

    public CategoriaUsuario getCategoriaNegocio() {
        Cliente cliente = getCliente();

        return cliente != null
                ? cliente.getCategoria()
                : null;
    }

    public EstadoRegistro getEstadoRegistro() {
        if (persona == null
                || persona.getEstadoRegistro() == null) {

            return EstadoRegistro.PENDIENTE_VALIDACION;
        }

        return persona.getEstadoRegistro();
    }

    public boolean estaValidadoComoCliente() {
        Cliente cliente = getCliente();

        return getEstadoRegistro()
                == EstadoRegistro.VALIDADO
                && cliente != null
                && cliente.estaAdmitido();
    }

    /**
     * Mantiene el formato de estado esperado por los DTO y el frontend.
     *
     * Puede devolver:
     *
     * BLOQUEADO
     * PENDIENTE_VALIDACION
     * VALIDADO
     * RECHAZADO
     */
    public String getEstadoExpuesto() {
        if (estaBloqueado()) {
            return EstadoUsuario.BLOQUEADO.name();
        }

        return getEstadoRegistro().name();
    }

    public void bloquear() {
        this.estado = EstadoUsuario.BLOQUEADO;
    }

    public void activar() {
        this.estado = EstadoUsuario.ACTIVO;
    }

    public void guardarSubasta(
            Subasta subasta
    ) {
        if (subasta != null
                && !guardadas.contains(subasta)) {

            guardadas.add(subasta);
        }
    }

    public void eliminarSubasta(
            Subasta subasta
    ) {
        guardadas.remove(subasta);
    }

    public boolean tieneSubastaGuardada(
            Subasta subasta
    ) {
        return guardadas.contains(subasta);
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

    public List<Subasta> getGuardadas() {
        return guardadas;
    }

    public void setEstado(
            EstadoUsuario estado
    ) {
        this.estado = estado;
    }

    public void setPassword(
            String password
    ) {
        this.password = password;
    }
}