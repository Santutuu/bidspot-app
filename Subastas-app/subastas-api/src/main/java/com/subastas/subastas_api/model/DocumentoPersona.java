package com.subastas.subastas_api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "documentos_persona")
public class DocumentoPersona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long identificador;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoDocumentoPersona tipo;

    @Column(name = "ruta", nullable = false, columnDefinition = "TEXT")
    private String ruta;

    public DocumentoPersona() {
    }

    public DocumentoPersona(Persona persona,
                            TipoDocumentoPersona tipo,
                            String ruta) {
        this.persona = persona;
        this.tipo = tipo;
        this.ruta = ruta;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public Persona getPersona() {
        return persona;
    }

    public TipoDocumentoPersona getTipo() {
        return tipo;
    }

    public String getRuta() {
        return ruta;
    }
}