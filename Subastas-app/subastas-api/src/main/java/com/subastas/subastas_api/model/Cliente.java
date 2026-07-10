package com.subastas.subastas_api.model;

import com.subastas.subastas_api.converter.CategoriaUsuarioConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @Column(name = "identificador")
    private Long identificador;

    @Column(name = "numeropais")
    private Integer numeroPais;

    @Column(name = "admitido")
    private String admitido;

    @Convert(converter = CategoriaUsuarioConverter.class)
    @Column(name = "categoria")
    private CategoriaUsuario categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verificador", nullable = false)
    private Empleado verificador;

    public Cliente() {
    }

    public Long getIdentificador() {
        return identificador;
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

    /**
     * Indica si la empresa habilitó al cliente para operar.
     *
     * La base legacy guarda este valor como "si" / "no".
     */
    public boolean estaAdmitido() {
        return "si".equalsIgnoreCase(admitido);
    }
}