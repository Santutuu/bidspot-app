package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String mail;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoUsuario estado = EstadoUsuario.PENDIENTE_VALIDACION;

    private String frenteDNIUrl;

    private String dorsoDNIUrl;

    @Embedded
    private Domicilio domicilio;

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

    public Usuario(String nombre,
                   String apellido,
                   String mail,
                   String password,
                   Rol rol,
                   EstadoUsuario estado,
                   String frenteDNIUrl,
                   String dorsoDNIUrl,
                   Domicilio domicilio) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.password = password;
        this.rol = rol;
        this.estado = estado;
        this.frenteDNIUrl = frenteDNIUrl;
        this.dorsoDNIUrl = dorsoDNIUrl;
        this.domicilio = domicilio;
    }

    public void agregarMedioDePago(MedioDePago medioDePago) {
        mediosDePago.add(medioDePago);
        medioDePago.setUsuario(this);
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getMail() {
        return mail;
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

    public String getFrenteDNIUrl() {
        return frenteDNIUrl;
    }

    public String getDorsoDNIUrl() {
        return dorsoDNIUrl;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setEstado(EstadoUsuario estado) {
        this.estado = estado;
    }
}