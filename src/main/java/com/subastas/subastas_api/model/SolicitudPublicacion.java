package com.subastas.subastas_api.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class SolicitudPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSolicitud;

    @ManyToOne
    private Usuario usuario;

    private String titulo;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    private String descripcion;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<AccionRequerida> accionesRequeridas;

    public SolicitudPublicacion() {
    }

    public SolicitudPublicacion(Usuario usuario,
                                String titulo,
                                EstadoSolicitud estado,
                                String descripcion,
                                List<AccionRequerida> accionesRequeridas) {
        this.usuario = usuario;
        this.titulo = titulo;
        this.estado = estado;
        this.descripcion = descripcion;
        this.accionesRequeridas = accionesRequeridas;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<AccionRequerida> getAccionesRequeridas() {
        return accionesRequeridas;
    }

    public void setAccionesRequeridas(List<AccionRequerida> accionesRequeridas) {
        this.accionesRequeridas = accionesRequeridas;
    }
}