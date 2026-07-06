package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItem;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 2000)
    private String descripcion;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @ElementCollection
    @CollectionTable(
            name = "item_imagenes",
            joinColumns = @JoinColumn(name = "item_id")
    )
    @Column(name = "imagen_url")
    private List<String> imagenesUrl;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    private EstadoItem estado = EstadoItem.PROPUESTO;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "poliza_id")
    private Poliza poliza;

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
        this.imagenUrl = imagenUrl;
        this.imagenesUrl = imagenesUrl;
        this.categoria = categoria;
        this.usuario = usuario;
        this.estado = EstadoItem.PROPUESTO;
    }

    public Long getIdItem() {
        return idItem;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public List<String> getImagenesUrl() {
        return imagenesUrl;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public EstadoItem getEstado() {
        return estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Poliza getPoliza() {
        return poliza;
    }

    public String getPrimeraImagen() {
        if (imagenesUrl != null) {
            return imagenesUrl.stream()
                    .filter(imagen -> imagen != null && !imagen.isBlank())
                    .findFirst()
                    .orElse(imagenUrl);
        }

        return imagenUrl;
    }

    public void setEstado(EstadoItem estado) {
        this.estado = estado;
    }

    public void setPoliza(Poliza poliza) {
        this.poliza = poliza;
    }

    public void marcarComoVendido() {
        this.estado = EstadoItem.VENDIDO;
    }
}
