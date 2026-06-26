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

    public Item() {
    }

    public Item(String titulo,
                String descripcion,
                List<String> imagenesUrl,
                Categoria categoria,
                Usuario usuario) {
        this.titulo = titulo;
        this.descripcion = descripcion;
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

    public String getPrimeraImagen() {
        if (imagenesUrl != null && !imagenesUrl.isEmpty()) {
            return imagenesUrl.get(0);
        }
        return null;
    }

    public void setEstado(EstadoItem estado) {
        this.estado = estado;
    }

    public void marcarComoVendido() {
        this.estado = EstadoItem.VENDIDO;
    }
}