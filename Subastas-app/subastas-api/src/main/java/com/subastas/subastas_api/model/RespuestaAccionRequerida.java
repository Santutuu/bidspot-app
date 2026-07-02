package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class RespuestaAccionRequerida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRespuesta;

    @ManyToOne
    @JoinColumn(name = "solicitud_id", nullable = false)
    private SolicitudPublicacion solicitudPublicacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccionRequerida accion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoRespuestaAccion tipoRespuesta;

    private Boolean aceptada;

    @Column(length = 1000)
    private String comentario;

    private String archivoUrl;

    private Float montoAseguradoSolicitado;

    private LocalDateTime fechaRespuesta;

    public RespuestaAccionRequerida() {
    }

    public RespuestaAccionRequerida(SolicitudPublicacion solicitudPublicacion,
                                    AccionRequerida accion,
                                    TipoRespuestaAccion tipoRespuesta,
                                    Boolean aceptada,
                                    String comentario,
                                    String archivoUrl,
                                    Float montoAseguradoSolicitado) {
        this.solicitudPublicacion = solicitudPublicacion;
        this.accion = accion;
        this.tipoRespuesta = tipoRespuesta;
        this.aceptada = aceptada;
        this.comentario = comentario;
        this.archivoUrl = archivoUrl;
        this.montoAseguradoSolicitado = montoAseguradoSolicitado;
        this.fechaRespuesta = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        this.fechaRespuesta = LocalDateTime.now();
    }

    public Long getIdRespuesta() {
        return idRespuesta;
    }

    public SolicitudPublicacion getSolicitudPublicacion() {
        return solicitudPublicacion;
    }

    public AccionRequerida getAccion() {
        return accion;
    }

    public TipoRespuestaAccion getTipoRespuesta() {
        return tipoRespuesta;
    }

    public Boolean getAceptada() {
        return aceptada;
    }

    public String getComentario() {
        return comentario;
    }

    public String getArchivoUrl() {
        return archivoUrl;
    }

    public Float getMontoAseguradoSolicitado() {
        return montoAseguradoSolicitado;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }
}