package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItem;

    private String titulo;

    @Column(length = 2000)
    private String descripcion;

    /*
     * URLs de las imágenes del artículo.
     * Se almacenan en una tabla auxiliar generada por JPA.
     */
    @ElementCollection
    @CollectionTable(
            name = "item_imagenes",
            joinColumns = @JoinColumn(name = "item_id")
    )
    @Column(name = "imagen_url")
    private List<String> imagenesUrl;

    @Enumerated(EnumType.STRING)
    private Categoria categoria;
    public Item() {
    }

    public Item(String titulo,
                String descripcion,
                List<String> imagenesUrl) {

        this.titulo = titulo;
        this.descripcion = descripcion;
        this.imagenesUrl = imagenesUrl;
    }

    public Long getIdItem() {
        return idItem;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getImagenesUrl() {
        return imagenesUrl;
    }

    public void setImagenesUrl(List<String> imagenesUrl) {
        this.imagenesUrl = imagenesUrl;
    }

    public String getPrimeraImagen() {
        if (imagenesUrl != null && !imagenesUrl.isEmpty()) {
            return imagenesUrl.get(0);
        }
        // Retorna null o una URL por defecto si prefieres
        return null;
    }


}