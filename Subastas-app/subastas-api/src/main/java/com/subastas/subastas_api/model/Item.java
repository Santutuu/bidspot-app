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

    // Compatibilidad con los datos actuales de la BD
    @Column(name = "imagen_url")
    private String imagenUrl;

    // Preparado para soportar múltiples imágenes en el futuro
    @ElementCollection
    @CollectionTable(
            name = "item_imagenes",
            joinColumns = @JoinColumn(name = "item_id")
    )
    @Column(name = "imagen_url")
    private List<String> imagenesUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoItem estado = EstadoItem.PROPUESTO;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
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

    /**
     * Devuelve la primera imagen disponible.
     * Si existen múltiples imágenes utiliza la primera.
     * Si todavía no fueron migradas, utiliza la columna imagen_url.
     */
    public String getPrimeraImagen() {
        if (imagenesUrl != null && !imagenesUrl.isEmpty()) {
            return imagenesUrl.get(0);
        }

        return imagenUrl;
    }

    public void setEstado(EstadoItem estado) {
        this.estado = estado;
    }

    public void marcarComoVendido() {
        this.estado = EstadoItem.VENDIDO;
    }
}