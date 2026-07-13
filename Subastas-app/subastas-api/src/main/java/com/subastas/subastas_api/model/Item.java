package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificador")
    private Long idItem;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "titulo")
    private String titulo;

    @Column(
            name = "descripcioncatalogo",
            length = 500
    )
    private String descripcion;

    /*
     * Campo obligatorio del modelo legacy.
     *
     * Originalmente representa una descripción extensa o
     * una referencia al documento descriptivo firmado.
     */
    @Column(
            name = "descripcioncompleta",
            nullable = false,
            length = 300
    )
    private String descripcionCompleta;

    @Column(name = "disponible")
    private String disponible = "si";

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private Categoria categoria;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "revisor",
            nullable = false
    )
    private Empleado revisor;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "duenio",
            nullable = false
    )
    private Duenio duenio;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "seguro",
            referencedColumnName = "nropoliza"
    )
    private Poliza poliza;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "solicitud_publicacion_id",
            unique = true
    )
    private SolicitudPublicacion solicitudPublicacion;

    @OneToMany(
            mappedBy = "producto",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Foto> fotos =
            new ArrayList<>();

    /*
     * Compatibilidad temporal con código anterior.
     *
     * No se persiste en productos.
     */
    @Transient
    private Usuario usuario;

    public Item() {
    }

    public Item(
            String titulo,
            String descripcion,
            String imagenUrl,
            List<String> imagenesUrl,
            Categoria categoria,
            Usuario usuario
    ) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.descripcionCompleta =
                limitarDescripcionCompleta(descripcion);

        this.categoria = categoria;
        this.usuario = usuario;
        this.disponible = "si";
        this.fecha = LocalDate.now();

        if (imagenUrl != null
                && !imagenUrl.isBlank()) {

            agregarFoto(imagenUrl);
        }

        if (imagenesUrl != null) {
            imagenesUrl.forEach(this::agregarFoto);
        }
    }

    public Long getIdItem() {
        return idItem;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getTitulo() {
        if (titulo != null
                && !titulo.isBlank()) {

            return titulo;
        }

        return descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getDescripcionCompleta() {
        return descripcionCompleta;
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
        if ("no".equalsIgnoreCase(
                disponible
        )) {
            return EstadoItem.VENDIDO;
        }

        return EstadoItem.PROPUESTO;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Empleado getRevisor() {
        return revisor;
    }

    public Duenio getDuenio() {
        return duenio;
    }

    public Poliza getPoliza() {
        return poliza;
    }

    public SolicitudPublicacion
    getSolicitudPublicacion() {
        return solicitudPublicacion;
    }

    public List<Foto> getFotos() {
        return fotos;
    }

    public String getPrimeraImagen() {
        if (fotos == null
                || fotos.isEmpty()) {

            return null;
        }

        return fotos.get(0).getFoto();
    }

    public void setFecha(
            LocalDate fecha
    ) {
        this.fecha = fecha;
    }

    public void setEstado(
            EstadoItem estado
    ) {
        if (estado == EstadoItem.VENDIDO) {
            this.disponible = "no";
        } else {
            this.disponible = "si";
        }
    }

    public void setDescripcionCompleta(
            String descripcionCompleta
    ) {
        this.descripcionCompleta =
                limitarDescripcionCompleta(
                        descripcionCompleta
                );
    }

    public void setRevisor(
            Empleado revisor
    ) {
        this.revisor = revisor;
    }

    public void setPoliza(
            Poliza poliza
    ) {
        this.poliza = poliza;
    }

    public void setDuenio(
            Duenio duenio
    ) {
        this.duenio = duenio;
    }

    public void setUsuario(
            Usuario usuario
    ) {
        this.usuario = usuario;
    }

    public void setSolicitudPublicacion(
            SolicitudPublicacion solicitudPublicacion
    ) {
        this.solicitudPublicacion =
                solicitudPublicacion;
    }

    public void agregarFoto(
            String url
    ) {
        if (url == null
                || url.isBlank()) {

            return;
        }

        /*
         * Evita duplicar la primera imagen cuando imagenUrl
         * también aparece dentro de imagenesUrl.
         */
        boolean yaExiste = fotos.stream()
                .anyMatch(foto ->
                        url.equals(foto.getFoto())
                );

        if (yaExiste) {
            return;
        }

        Foto foto = new Foto(url);

        foto.setProducto(this);

        this.fotos.add(foto);
    }

    public void marcarComoVendido() {
        this.disponible = "no";
    }

    private String limitarDescripcionCompleta(
            String valor
    ) {
        if (valor == null
                || valor.isBlank()) {

            return "Descripción completa pendiente";
        }

        String texto = valor.trim();

        if (texto.length() <= 300) {
            return texto;
        }

        return texto.substring(0, 300);
    }
}