package com.subastas.subastas_api.model;

import com.subastas.subastas_api.converter.CategoriaUsuarioConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {

    /*
     * Clave primaria compartida con Persona.
     *
     * Para un Cliente nuevo, este valor debe permanecer null antes
     * de persistir. @MapsId lo obtiene automáticamente desde Persona.
     */
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

    public Cliente() {
    }

    public Cliente(Persona persona,
                   CategoriaUsuario categoria,
                   Empleado verificador) {
        this.persona = persona;

        /*
         * No asignar identificador manualmente.
         * @MapsId lo copiará desde persona.getIdPersona().
         */
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
}