package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "personas")
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long idPersona;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "documento", nullable = false, unique = true)
    private String documento;

    @Column(name = "mail", unique = true)
    private String mail;

    @Embedded
    private Domicilio domicilio;

    @Column(name = "estado")
    private String estado = "activo";

    @OneToOne(mappedBy = "persona")
    private Usuario usuario;

    @OneToMany(
            mappedBy = "persona",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DocumentoPersona> documentos = new ArrayList<>();

    public Persona() {
    }

    public Persona(String nombre,
                   String apellido,
                   String documento,
                   String mail,
                   Domicilio domicilio) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.mail = mail;
        this.domicilio = domicilio;
        this.estado = "activo";
    }

    public void agregarDocumento(DocumentoPersona documentoPersona) {
        documentos.add(documentoPersona);
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

    public String getDocumento() {
        return documento;
    }

    public String getMail() {
        return mail;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public String getEstado() {
        return estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<DocumentoPersona> getDocumentos() {
        return documentos;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}