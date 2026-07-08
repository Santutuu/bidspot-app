package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long idItem;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "descripcioncatalogo", length = 500)
    private String descripcion;

    @Column(name = "disponible")
    private String disponible = "si";

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "duenio")
    private Duenio duenio;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seguro", referencedColumnName = "nropoliza")
    private Poliza poliza;

    @OneToOne
    @JoinColumn(name = "solicitud_publicacion_id", unique = true)
    private SolicitudPublicacion solicitudPublicacion;

    @OneToMany(
            mappedBy = "producto",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Foto> fotos = new ArrayList<>();

    @Transient
    private Usuario usuario;

    public Item() {
    }

    public Item(String titulo,
                String descripcion,
                String imagenUrl,
                List<String> imagenesUrl,
                Categoria categoria,
                Usuario usuario) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.usuario = usuario;
        this.disponible = "si";

        if (imagenUrl != null && !imagenUrl.isBlank()) {
            agregarFoto(imagenUrl);
        }

        if (imagenesUrl != null) {
            imagenesUrl.forEach(this::agregarFoto);
        }
    }

    public Long getIdItem() {
        return idItem;
    }

    public String getTitulo() {
        if (titulo != null && !titulo.isBlank()) {
            return titulo;
        }

        return descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagenUrl() {
        return getPrimeraImagen();
    }

    public List<String> getImagenesUrl() {
        return fotos.stream()
                .map(Foto::getFoto)
                .toList();
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public EstadoItem getEstado() {
        if ("no".equalsIgnoreCase(disponible)) {
            return EstadoItem.VENDIDO;
        }

        return EstadoItem.PROPUESTO;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Duenio getDuenio() {
        return duenio;
    }

    public Poliza getPoliza() {
        return poliza;
    }

    public SolicitudPublicacion getSolicitudPublicacion() {
        return solicitudPublicacion;
    }

    public List<Foto> getFotos() {
        return fotos;
    }

    public String getPrimeraImagen() {
        if (fotos == null || fotos.isEmpty()) {
            return null;
        }

        return fotos.get(0).getFoto();
    }

    public void setEstado(EstadoItem estado) {
        if (estado == EstadoItem.VENDIDO) {
            this.disponible = "no";
        } else {
            this.disponible = "si";
        }
    }

    public void setPoliza(Poliza poliza) {
        this.poliza = poliza;
    }

    public void setDuenio(Duenio duenio) {
        this.duenio = duenio;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void agregarFoto(String url) {
        if (url == null || url.isBlank()) {
            return;
        }

        Foto foto = new Foto(url);
        foto.setProducto(this);
        this.fotos.add(foto);
    }

    public void marcarComoVendido() {
        this.disponible = "no";
    }
}