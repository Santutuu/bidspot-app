package com.subastas.subastas_api.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "solicitud_publicacion")
public class SolicitudPublicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Long idSolicitud;

    /*
     * La solicitud pertenece comercialmente a Cliente.
     * Usuario solamente autentica la petición.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /*
     * Se asigna recién cuando el usuario acepta las
     * condiciones de venta.
     *
     * Item se encuentra mapeado a productos, tabla legacy.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", unique = true)
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private Categoria categoria;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(
            name = "descripcion",
            length = 2000,
            nullable = false
    )
    private String descripcion;

    @ElementCollection
    @CollectionTable(
            name = "solicitud_publicacion_imagenes",
            joinColumns = @JoinColumn(name = "solicitud_id")
    )
    @Column(name = "imagen_url", length = 1000)
    private List<String> imagenesUrl =
            new ArrayList<>();

    @Column(name = "declaracion_propiedad", nullable = false)
    private boolean declaracionPropiedad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoSolicitud estado =
            EstadoSolicitud.PENDIENTE_REVISION;

    /*
     * Información cargada cuando la empresa muestra interés.
     */
    @Column(name = "direccion_deposito", length = 500)
    private String direccionDeposito;

    @Column(name = "fecha_limite_envio")
    private LocalDate fechaLimiteEnvio;

    /*
     * Confirmación expresa del usuario.
     */
    @Column(name = "acepta_devolucion_con_cargo")
    private Boolean aceptaDevolucionConCargo;

    @Column(name = "fecha_aceptacion_envio")
    private LocalDateTime fechaAceptacionEnvio;

    /*
     * La empresa completa esta fecha cuando recibe el producto.
     */
    @Column(name = "fecha_recepcion")
    private LocalDateTime fechaRecepcion;

    /*
     * Ubicación actual simple, sin historial.
     */
    @Column(name = "ubicacion_actual", length = 500)
    private String ubicacionActual;

    @Column(name = "fecha_actualizacion_ubicacion")
    private LocalDateTime fechaActualizacionUbicacion;

    @Column(name = "motivo_rechazo", length = 1500)
    private String motivoRechazo;

    @OneToMany(
            mappedBy = "solicitud",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("fechaCreacion ASC")
    private List<AccionSolicitudPublicacion> acciones =
            new ArrayList<>();

    @OneToOne(
            mappedBy = "solicitud",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private PropuestaCondicionesVenta propuestaVenta;

    @OneToOne(
            mappedBy = "solicitud",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private DevolucionSolicitud devolucion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public SolicitudPublicacion() {
    }

    public SolicitudPublicacion(
            Cliente cliente,
            Categoria categoria,
            String titulo,
            String descripcion,
            List<String> imagenesUrl,
            boolean declaracionPropiedad
    ) {
        this.cliente = cliente;
        this.categoria = categoria;
        this.titulo = titulo;
        this.descripcion = descripcion;

        this.imagenesUrl = imagenesUrl != null
                ? new ArrayList<>(imagenesUrl)
                : new ArrayList<>();

        this.declaracionPropiedad = declaracionPropiedad;
        this.estado = EstadoSolicitud.PENDIENTE_REVISION;
    }

    @PrePersist
    private void prePersist() {
        LocalDateTime ahora = LocalDateTime.now();

        if (estado == null) {
            estado = EstadoSolicitud.PENDIENTE_REVISION;
        }

        if (fechaCreacion == null) {
            fechaCreacion = ahora;
        }

        fechaActualizacion = ahora;
    }

    @PreUpdate
    private void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    /*
     * La empresa manifiesta interés.
     */
    public void mostrarInteres(
            String direccionDeposito,
            LocalDate fechaLimiteEnvio
    ) {
        exigirEstado(EstadoSolicitud.PENDIENTE_REVISION);

        this.direccionDeposito = direccionDeposito;
        this.fechaLimiteEnvio = fechaLimiteEnvio;
        this.estado = EstadoSolicitud.INTERES_EMPRESA;
        this.motivoRechazo = null;
    }

    /*
     * El usuario acepta enviar el producto y la devolución
     * con cargo si el bien fuera rechazado.
     */
    public void aceptarEnvioInspeccion() {
        exigirEstado(EstadoSolicitud.INTERES_EMPRESA);

        this.aceptaDevolucionConCargo = true;
        this.fechaAceptacionEnvio = LocalDateTime.now();
        this.estado = EstadoSolicitud.PENDIENTE_ENVIO;
    }

    /*
     * La empresa confirma que recibió el producto.
     */
    public void confirmarRecepcion(
            String ubicacionActual
    ) {
        exigirEstado(EstadoSolicitud.PENDIENTE_ENVIO);

        this.fechaRecepcion = LocalDateTime.now();
        actualizarUbicacion(ubicacionActual);
        this.estado = EstadoSolicitud.EN_INSPECCION;
    }

    /*
     * La empresa crea una propuesta de venta.
     */
    public void proponerCondiciones(
            PropuestaCondicionesVenta propuestaVenta
    ) {
        exigirEstado(EstadoSolicitud.EN_INSPECCION);

        this.propuestaVenta = propuestaVenta;
        this.estado =
                EstadoSolicitud.PENDIENTE_CONDICIONES_VENTA;
    }

    /*
     * Se ejecutará cuando el usuario acepte las condiciones
     * y el backend cree Item + ItemCatalogo legacy.
     */
    public void condicionesAceptadas(Item item) {
        exigirEstado(
                EstadoSolicitud.PENDIENTE_CONDICIONES_VENTA
        );

        this.item = item;
        this.estado = EstadoSolicitud.PENDIENTE_POLIZA;
    }

    public void marcarListaParaSubasta() {
        exigirEstado(EstadoSolicitud.PENDIENTE_POLIZA);
        this.estado = EstadoSolicitud.LISTA_PARA_SUBASTA;
    }

    /*
     * Rechazo antes de que el producto llegara a la empresa.
     */
    public void rechazarSinDevolucion(String motivo) {
        if (estado != EstadoSolicitud.PENDIENTE_REVISION
                && estado != EstadoSolicitud.INTERES_EMPRESA) {

            throw new IllegalStateException(
                    "La solicitud ya no puede rechazarse sin devolución"
            );
        }

        this.motivoRechazo = motivo;
        this.estado = EstadoSolicitud.RECHAZADA;
        cancelarAccionesPendientes();
    }

    /*
     * Rechazo cuando el producto está físicamente en poder
     * de la empresa.
     */
    public void iniciarDevolucion(
            String motivo,
            DevolucionSolicitud devolucion
    ) {
        if (estado != EstadoSolicitud.EN_INSPECCION
                && estado !=
                EstadoSolicitud.PENDIENTE_CONDICIONES_VENTA) {

            throw new IllegalStateException(
                    "La solicitud no admite una devolución"
            );
        }

        this.motivoRechazo = motivo;
        this.devolucion = devolucion;
        this.estado = EstadoSolicitud.DEVOLUCION_PENDIENTE;
        cancelarAccionesPendientes();
    }

    public void marcarDevuelta() {
        exigirEstado(EstadoSolicitud.DEVOLUCION_PENDIENTE);
        this.estado = EstadoSolicitud.DEVUELTA;
    }

    public void cancelar() {
        if (estado != EstadoSolicitud.PENDIENTE_REVISION
                && estado != EstadoSolicitud.INTERES_EMPRESA) {

            throw new IllegalStateException(
                    "La solicitud ya no puede cancelarse directamente"
            );
        }

        this.estado = EstadoSolicitud.CANCELADA;
        cancelarAccionesPendientes();
    }

    public void actualizarUbicacion(String ubicacionActual) {
        if (ubicacionActual == null
                || ubicacionActual.isBlank()) {

            throw new IllegalArgumentException(
                    "La ubicación es obligatoria"
            );
        }

        this.ubicacionActual = ubicacionActual.trim();
        this.fechaActualizacionUbicacion =
                LocalDateTime.now();
    }

    public void agregarAccion(
            AccionSolicitudPublicacion accion
    ) {
        if (accion == null) {
            return;
        }

        boolean yaExistePendiente = acciones.stream()
                .anyMatch(actual ->
                        actual.getTipo() == accion.getTipo()
                                && actual.estaPendiente()
                );

        if (yaExistePendiente) {
            throw new IllegalStateException(
                    "Ya existe una acción pendiente del tipo "
                            + accion.getTipo()
            );
        }

        acciones.add(accion);
    }

    public AccionSolicitudPublicacion
    buscarAccionPendiente(TipoAccionSolicitud tipo) {

        return acciones.stream()
                .filter(AccionSolicitudPublicacion::estaPendiente)
                .filter(accion -> accion.getTipo() == tipo)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "No existe una acción pendiente de tipo "
                                        + tipo
                        )
                );
    }

    private void cancelarAccionesPendientes() {
        acciones.stream()
                .filter(AccionSolicitudPublicacion::estaPendiente)
                .forEach(AccionSolicitudPublicacion::cancelar);
    }

    private void exigirEstado(EstadoSolicitud esperado) {
        if (estado != esperado) {
            throw new IllegalStateException(
                    "La solicitud debe encontrarse en estado "
                            + esperado
                            + " y actualmente está en "
                            + estado
            );
        }
    }

    public Long getIdSolicitud() {
        return idSolicitud;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Item getItem() {
        return item;
    }

    public Categoria getCategoria() {
        return categoria;
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

    public boolean isDeclaracionPropiedad() {
        return declaracionPropiedad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public String getDireccionDeposito() {
        return direccionDeposito;
    }

    public LocalDate getFechaLimiteEnvio() {
        return fechaLimiteEnvio;
    }

    public Boolean getAceptaDevolucionConCargo() {
        return aceptaDevolucionConCargo;
    }

    public LocalDateTime getFechaAceptacionEnvio() {
        return fechaAceptacionEnvio;
    }

    public LocalDateTime getFechaRecepcion() {
        return fechaRecepcion;
    }

    public String getUbicacionActual() {
        return ubicacionActual;
    }

    public LocalDateTime getFechaActualizacionUbicacion() {
        return fechaActualizacionUbicacion;
    }

    public String getMotivoRechazo() {
        return motivoRechazo;
    }

    public List<AccionSolicitudPublicacion> getAcciones() {
        return acciones;
    }

    public PropuestaCondicionesVenta getPropuestaVenta() {
        return propuestaVenta;
    }

    public DevolucionSolicitud getDevolucion() {
        return devolucion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public String getPrimeraImagen() {
        if (imagenesUrl == null || imagenesUrl.isEmpty()) {
            return null;
        }

        return imagenesUrl.get(0);
    }
}