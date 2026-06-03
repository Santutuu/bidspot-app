package com.subastas.subastas_api.model;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.List;



@Entity
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String nombre;
    private String apellido;
    private String mail;
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
    private List<Subasta> guardadas;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cuenta_id")
    private CuentaBanco cuenta;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedioDePago> mediosDePago;

    @OneToMany(mappedBy = "usuario")
    private List<Puja> pujas;

    public Usuario() {}

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void agregarMedioDePago(MedioDePago medioDePago) {
        mediosDePago.add(medioDePago);
        medioDePago.setUsuario(this);
    }
}