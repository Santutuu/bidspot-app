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

    @Column(
            name = "documento",
            nullable = false,
            unique = true
    )
    private String documento;

    @Column(
            name = "mail",
            unique = true
    )
    private String mail;

    @Embedded
    private Domicilio domicilio;

    /*
     * Campo legacy que indica si la persona está activa.
     *
     * No debe confundirse con el proceso de aprobación del registro.
     */
    @Column(name = "estado")
    private String estado = "activo";

    /*
     * Estado del proceso de validación empresarial.
     */
    @Enumerated(EnumType.STRING)
    @Column(
            name = "estado_registro",
            nullable = false
    )
    private EstadoRegistro estadoRegistro =
            EstadoRegistro.PENDIENTE_VALIDACION;

    @OneToOne(mappedBy = "persona")
    private Usuario usuario;

    @OneToOne(
            mappedBy = "persona",
            fetch = FetchType.LAZY
    )
    private Cliente cliente;

    @OneToMany(
            mappedBy = "persona",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DocumentoPersona> documentos =
            new ArrayList<>();

    public Persona() {
    }

    public Persona(
            String nombre,
            String apellido,
            String documento,
            String mail,
            Domicilio domicilio
    ) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.mail = mail;
        this.domicilio = domicilio;
        this.estado = "activo";
        this.estadoRegistro =
                EstadoRegistro.PENDIENTE_VALIDACION;
    }

    public void agregarDocumento(
            DocumentoPersona documentoPersona
    ) {
        if (documentoPersona == null) {
            return;
        }

        documentos.add(documentoPersona);
    }

    public void marcarRegistroPendiente() {
        this.estadoRegistro =
                EstadoRegistro.PENDIENTE_VALIDACION;
    }

    public void aprobarRegistro() {
        this.estadoRegistro =
                EstadoRegistro.VALIDADO;
    }

    public void rechazarRegistro() {
        this.estadoRegistro =
                EstadoRegistro.RECHAZADO;
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

    public EstadoRegistro getEstadoRegistro() {
        return estadoRegistro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public List<DocumentoPersona> getDocumentos() {
        return documentos;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setEstadoRegistro(
            EstadoRegistro estadoRegistro
    ) {
        this.estadoRegistro = estadoRegistro;
    }
}