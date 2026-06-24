package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersona;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String mail;

    private String frenteDNIUrl;

    private String dorsoDNIUrl;

    @Embedded
    private Domicilio domicilio;

    @OneToOne(mappedBy = "persona")
    private Usuario usuario;

    public Persona() {
    }

    public Persona(String nombre,
                   String apellido,
                   String mail,
                   String frenteDNIUrl,
                   String dorsoDNIUrl,
                   Domicilio domicilio) {

        this.nombre = nombre;
        this.apellido = apellido;
        this.mail = mail;
        this.frenteDNIUrl = frenteDNIUrl;
        this.dorsoDNIUrl = dorsoDNIUrl;
        this.domicilio = domicilio;
    }

    public Long getIdPersona() {
        return idPersona;
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

    public String getFrenteDNIUrl() {
        return frenteDNIUrl;
    }

    public String getDorsoDNIUrl() {
        return dorsoDNIUrl;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}