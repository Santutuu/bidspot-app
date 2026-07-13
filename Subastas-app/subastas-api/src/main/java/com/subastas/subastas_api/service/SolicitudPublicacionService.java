package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.publicacion.ConfigurarDevolucionRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.ResolverAccionSolicitudRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionDetalleDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionResumenDTO;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.AccionSolicitudPublicacionRepository;
import com.subastas.subastas_api.repository.DevolucionSolicitudRepository;
import com.subastas.subastas_api.repository.MedioDePagoRepository;
import com.subastas.subastas_api.repository.PropuestaCondicionesVentaRepository;
import com.subastas.subastas_api.repository.SolicitudPublicacionRepository;
import com.subastas.subastas_api.repository.TarjetaCreditoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SolicitudPublicacionService {

    private static final int MINIMO_IMAGENES = 6;

    private final SolicitudPublicacionRepository
            solicitudRepository;

    private final AccionSolicitudPublicacionRepository
            accionRepository;

    private final PropuestaCondicionesVentaRepository
            propuestaRepository;

    private final DevolucionSolicitudRepository
            devolucionRepository;

    private final MedioDePagoRepository
            medioDePagoRepository;

    private final TarjetaCreditoRepository
            tarjetaRepository;

    private final SolicitudPublicacionMapper
            mapper;

    public SolicitudPublicacionService(
            SolicitudPublicacionRepository solicitudRepository,
            AccionSolicitudPublicacionRepository accionRepository,
            PropuestaCondicionesVentaRepository propuestaRepository,
            DevolucionSolicitudRepository devolucionRepository,
            MedioDePagoRepository medioDePagoRepository,
            TarjetaCreditoRepository tarjetaRepository,
            SolicitudPublicacionMapper mapper
    ) {
        this.solicitudRepository =
                solicitudRepository;

        this.accionRepository =
                accionRepository;

        this.propuestaRepository =
                propuestaRepository;

        this.devolucionRepository =
                devolucionRepository;

        this.medioDePagoRepository =
                medioDePagoRepository;

        this.tarjetaRepository =
                tarjetaRepository;

        this.mapper = mapper;
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO crearSolicitud(
            Usuario usuario,
            SolicitudPublicacionRequestDTO request
    ) {
        Cliente cliente =
                validarCliente(usuario);

        validarCreacion(request);

        boolean tieneTarjetaPesos =
                tarjetaRepository
                        .findByCliente(cliente)
                        .stream()
                        .map(TarjetaCredito::getMoneda)
                        .anyMatch(moneda ->
                                moneda == Moneda.PESOS
                                        || moneda == Moneda.ARS
                        );

        if (!tieneTarjetaPesos) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Debe registrar al menos una tarjeta de crédito en pesos"
            );
        }

        List<String> imagenesValidas =
                normalizarImagenes(
                        request.getImagenesUrl()
                );

        SolicitudPublicacion solicitud =
                new SolicitudPublicacion(
                        cliente,
                        request.getCategoria(),
                        request.getTitulo().trim(),
                        request.getDescripcion().trim(),
                        imagenesValidas,
                        request.isDeclaracionPropiedad()
                );

        SolicitudPublicacion guardada =
                solicitudRepository.save(solicitud);

        return mapper.toDetalle(guardada);
    }

    @Transactional(readOnly = true)
    public List<SolicitudPublicacionResumenDTO>
    listarMisSolicitudes(
            Usuario usuario
    ) {
        Cliente cliente =
                validarCliente(usuario);

        return solicitudRepository
                .findByClienteOrderByFechaCreacionDesc(
                        cliente
                )
                .stream()
                .map(mapper::toResumen)
                .toList();
    }

    @Transactional(readOnly = true)
    public SolicitudPublicacionDetalleDTO obtenerDetalle(
            Long idSolicitud,
            Usuario usuario
    ) {
        Cliente cliente =
                validarCliente(usuario);

        SolicitudPublicacion solicitud =
                obtenerDelCliente(
                        idSolicitud,
                        cliente
                );

        return mapper.toDetalle(solicitud);
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO resolverAccion(
            Long idSolicitud,
            Long idAccion,
            Usuario usuario,
            ResolverAccionSolicitudRequestDTO request
    ) {
        if (request == null
                || request.getAceptada() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar si acepta o rechaza"
            );
        }

        Cliente cliente =
                validarCliente(usuario);

        SolicitudPublicacion solicitud =
                obtenerDelCliente(
                        idSolicitud,
                        cliente
                );

        AccionSolicitudPublicacion accion =
                accionRepository
                        .findByIdAccionAndSolicitud(
                                idAccion,
                                solicitud
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Acción no encontrada"
                                )
                        );

        if (!accion.estaPendiente()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La acción ya fue resuelta"
            );
        }

        switch (accion.getTipo()) {

            case ACEPTAR_ENVIO_INSPECCION -> {
                if (Boolean.TRUE.equals(
                        request.getAceptada()
                )) {
                    ejecutarDominio(
                            solicitud::aceptarEnvioInspeccion
                    );
                } else {
                    ejecutarDominio(
                            solicitud::cancelar
                    );
                }
            }

            case ACEPTAR_CONDICIONES_VENTA ->
                    resolverCondicionesVenta(
                            solicitud,
                            request
                    );

            case REVISAR_POLIZA ->
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "La póliza se resuelve desde los endpoints /poliza"
                    );

            case PAGAR_DEVOLUCION ->
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Configure y confirme la devolución desde los endpoints /devolucion"
                    );

            case COMPROBAR_ORIGEN_LICITO,
                 PROPUESTA_COLECCION -> {
                /*
                 * Por ahora se registra únicamente la respuesta.
                 */
            }
        }

        String comentario =
                obtenerComentarioAccion(
                        accion,
                        request
                );

        ejecutarDominio(() ->
                accion.completar(
                        request.getAceptada(),
                        comentario
                )
        );

        return mapper.toDetalle(solicitud);
    }

    private void resolverCondicionesVenta(
            SolicitudPublicacion solicitud,
            ResolverAccionSolicitudRequestDTO request
    ) {
        PropuestaCondicionesVenta propuesta =
                propuestaRepository
                        .findBySolicitud(solicitud)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "No existe una propuesta de venta"
                                )
                        );

        if (Boolean.TRUE.equals(
                request.getAceptada()
        )) {
            ejecutarDominio(
                    propuesta::aceptar
            );

            return;
        }

        String motivo =
                request.getComentario() == null
                        || request.getComentario().isBlank()
                        ? "Condiciones rechazadas por el usuario"
                        : request.getComentario().trim();

        ejecutarDominio(() ->
                propuesta.rechazar(motivo)
        );

        DevolucionSolicitud devolucion =
                new DevolucionSolicitud(
                        solicitud,
                        15000f,
                        Moneda.PESOS
                );

        devolucionRepository.save(devolucion);

        ejecutarDominio(() ->
                solicitud.iniciarDevolucion(
                        motivo,
                        devolucion
                )
        );

        crearAccion(
                solicitud,
                TipoAccionSolicitud.PAGAR_DEVOLUCION,
                "Pagar devolución",
                "Configure la dirección, el medio de pago y confirme el pago de la devolución."
        );
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO configurarDevolucion(
            Long idSolicitud,
            Usuario usuario,
            ConfigurarDevolucionRequestDTO request
    ) {
        Cliente cliente =
                validarCliente(usuario);

        SolicitudPublicacion solicitud =
                obtenerDelCliente(
                        idSolicitud,
                        cliente
                );

        DevolucionSolicitud devolucion =
                devolucionRepository
                        .findBySolicitud(solicitud)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No existe una devolución pendiente"
                                )
                        );

        if (request == null
                || request.getDireccionDestino() == null
                || request.getDireccionDestino().isBlank()
                || request.getIdMedioPago() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La dirección y el medio de pago son obligatorios"
            );
        }

        MedioDePago medioPago =
                medioDePagoRepository
                        .findByIdMedioPagoAndCliente(
                                request.getIdMedioPago(),
                                cliente
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Medio de pago no encontrado"
                                )
                        );

        ejecutarDominio(() ->
                devolucion.configurar(
                        request.getDireccionDestino()
                                .trim(),
                        medioPago
                )
        );

        return mapper.toDetalle(solicitud);
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO
    confirmarPagoDevolucion(
            Long idSolicitud,
            Usuario usuario
    ) {
        Cliente cliente =
                validarCliente(usuario);

        SolicitudPublicacion solicitud =
                obtenerDelCliente(
                        idSolicitud,
                        cliente
                );

        DevolucionSolicitud devolucion =
                devolucionRepository
                        .findBySolicitud(solicitud)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No existe una devolución pendiente"
                                )
                        );

        ejecutarDominio(
                devolucion::confirmarPago
        );

        accionRepository
                .findFirstBySolicitudAndTipoAndEstadoOrderByFechaCreacionDesc(
                        solicitud,
                        TipoAccionSolicitud.PAGAR_DEVOLUCION,
                        EstadoAccionSolicitud.PENDIENTE
                )
                .ifPresent(accion ->
                        accion.completar(
                                true,
                                "Pago de devolución confirmado"
                        )
                );

        return mapper.toDetalle(solicitud);
    }

    @Transactional
    public void cancelarSolicitud(
            Long idSolicitud,
            Usuario usuario
    ) {
        Cliente cliente =
                validarCliente(usuario);

        SolicitudPublicacion solicitud =
                obtenerDelCliente(
                        idSolicitud,
                        cliente
                );

        ejecutarDominio(
                solicitud::cancelar
        );
    }

    private void crearAccion(
            SolicitudPublicacion solicitud,
            TipoAccionSolicitud tipo,
            String titulo,
            String descripcion
    ) {
        boolean yaExiste =
                accionRepository
                        .existsBySolicitudAndTipoAndEstado(
                                solicitud,
                                tipo,
                                EstadoAccionSolicitud.PENDIENTE
                        );

        if (yaExiste) {
            return;
        }

        AccionSolicitudPublicacion accion =
                new AccionSolicitudPublicacion(
                        solicitud,
                        tipo,
                        titulo,
                        descripcion
                );

        ejecutarDominio(() ->
                solicitud.agregarAccion(accion)
        );

        accionRepository.save(accion);
    }

    private Cliente validarCliente(
            Usuario usuario
    ) {
        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        if (usuario.estaBloqueado()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La cuenta se encuentra bloqueada"
            );
        }

        if (!usuario.estaValidadoComoCliente()
                || usuario.getCliente() == null) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no está validado como cliente"
            );
        }

        return usuario.getCliente();
    }

    private SolicitudPublicacion obtenerDelCliente(
            Long idSolicitud,
            Cliente cliente
    ) {
        return solicitudRepository
                .findByIdSolicitudAndCliente(
                        idSolicitud,
                        cliente
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Solicitud no encontrada"
                        )
                );
    }

    private void validarCreacion(
            SolicitudPublicacionRequestDTO request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos son obligatorios"
            );
        }

        if (request.getCategoria() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La categoría es obligatoria"
            );
        }

        if (request.getTitulo() == null
                || request.getTitulo().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El título es obligatorio"
            );
        }

        if (request.getDescripcion() == null
                || request.getDescripcion().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripción es obligatoria"
            );
        }

        if (!request.isDeclaracionPropiedad()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe aceptar la declaración de propiedad"
            );
        }

        List<String> imagenesValidas =
                normalizarImagenes(
                        request.getImagenesUrl()
                );

        if (imagenesValidas.size()
                < MINIMO_IMAGENES) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe cargar al menos 6 imágenes válidas y diferentes"
            );
        }
    }

    /*
     * No existe un máximo de imágenes.
     *
     * Solamente se eliminan:
     * - null;
     * - cadenas vacías;
     * - URLs repetidas.
     */
    private List<String> normalizarImagenes(
            List<String> imagenesUrl
    ) {
        if (imagenesUrl == null) {
            return List.of();
        }

        return imagenesUrl
                .stream()
                .filter(url ->
                        url != null
                                && !url.isBlank()
                )
                .map(String::trim)
                .distinct()
                .toList();
    }

    private String obtenerComentarioAccion(
            AccionSolicitudPublicacion accion,
            ResolverAccionSolicitudRequestDTO request
    ) {
        if (request.getComentario() != null
                && !request.getComentario().isBlank()) {

            return request.getComentario()
                    .trim();
        }

        if (!Boolean.TRUE.equals(
                request.getAceptada()
        )) {
            return "Rechazado por el usuario";
        }

        return switch (accion.getTipo()) {

            case ACEPTAR_ENVIO_INSPECCION ->
                    "El usuario aceptó las condiciones de envío e inspección";

            case ACEPTAR_CONDICIONES_VENTA ->
                    "El usuario aceptó las condiciones de venta";

            case REVISAR_POLIZA ->
                    "El usuario aceptó la póliza";

            case PAGAR_DEVOLUCION ->
                    "El usuario confirmó la devolución";

            case COMPROBAR_ORIGEN_LICITO ->
                    "El usuario respondió la solicitud de origen lícito";

            case PROPUESTA_COLECCION ->
                    "El usuario respondió la propuesta de colección";
        };
    }

    private void ejecutarDominio(
            Runnable operacion
    ) {
        try {
            operacion.run();

        } catch (IllegalStateException
                 | IllegalArgumentException exception) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );
        }
    }
}