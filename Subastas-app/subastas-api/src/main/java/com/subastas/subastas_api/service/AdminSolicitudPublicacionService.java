package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.publicacion.ActualizarUbicacionRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.ConfirmarRecepcionRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.MostrarInteresesRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.ProponerCondicionesVentaRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.RechazarSolicitudRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionDetalleDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionResumenDTO;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.*;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class AdminSolicitudPublicacionService {

    private final SolicitudPublicacionRepository
            solicitudRepository;

    private final AccionSolicitudPublicacionRepository
            accionRepository;

    private final PropuestaCondicionesVentaRepository
            propuestaRepository;

    private final DevolucionSolicitudRepository
            devolucionRepository;

    private final SubastaRepository
            subastaRepository;

    private final ItemRepository
            itemRepository;

    private final ItemCatalogoRepository
            itemCatalogoRepository;

    private final EstadoItemCatalogoRepository
            estadoItemCatalogoRepository;

    private final DuenioRepository
            duenioRepository;

    private final EmpleadoRepository
            empleadoRepository;

    private final SolicitudPublicacionMapper
            mapper;

    public AdminSolicitudPublicacionService(
            SolicitudPublicacionRepository solicitudRepository,
            AccionSolicitudPublicacionRepository accionRepository,
            PropuestaCondicionesVentaRepository propuestaRepository,
            DevolucionSolicitudRepository devolucionRepository,
            SubastaRepository subastaRepository,
            ItemRepository itemRepository,
            ItemCatalogoRepository itemCatalogoRepository,
            EstadoItemCatalogoRepository estadoItemCatalogoRepository,
            DuenioRepository duenioRepository,
            EmpleadoRepository empleadoRepository,
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

        this.subastaRepository =
                subastaRepository;

        this.itemRepository =
                itemRepository;

        this.itemCatalogoRepository =
                itemCatalogoRepository;

        this.estadoItemCatalogoRepository =
                estadoItemCatalogoRepository;

        this.duenioRepository =
                duenioRepository;

        this.empleadoRepository =
                empleadoRepository;

        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public List<SolicitudPublicacionResumenDTO>
    listarSolicitudes(
            EstadoSolicitud estado,
            Categoria categoria
    ) {
        List<SolicitudPublicacion> solicitudes;

        if (estado != null
                && categoria != null) {

            solicitudes = solicitudRepository
                    .findByEstadoAndCategoriaOrderByFechaCreacionAsc(
                            estado,
                            categoria,
                            Pageable.unpaged()
                    )
                    .getContent();

        } else if (estado != null) {

            solicitudes = solicitudRepository
                    .findByEstadoOrderByFechaCreacionAsc(
                            estado,
                            Pageable.unpaged()
                    )
                    .getContent();

        } else if (categoria != null) {

            solicitudes = solicitudRepository
                    .findByCategoriaOrderByFechaCreacionAsc(
                            categoria,
                            Pageable.unpaged()
                    )
                    .getContent();

        } else {
            solicitudes =
                    solicitudRepository.findAll();

            solicitudes.sort(
                    Comparator.comparing(
                            SolicitudPublicacion::getFechaCreacion
                    )
            );
        }

        return solicitudes.stream()
                .map(mapper::toResumen)
                .toList();
    }

    @Transactional(readOnly = true)
    public SolicitudPublicacionDetalleDTO obtenerDetalle(
            Long idSolicitud
    ) {
        return mapper.toDetalle(
                obtenerSolicitud(idSolicitud)
        );
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO mostrarInteres(
            Long idSolicitud,
            MostrarInteresesRequestDTO request
    ) {
        if (request == null
                || request.getDireccionDeposito() == null
                || request.getDireccionDeposito().isBlank()
                || request.getFechaLimiteEnvio() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La dirección y la fecha límite son obligatorias"
            );
        }

        SolicitudPublicacion solicitud =
                obtenerSolicitud(idSolicitud);

        ejecutarDominio(() ->
                solicitud.mostrarInteres(
                        request.getDireccionDeposito()
                                .trim(),
                        request.getFechaLimiteEnvio()
                )
        );

        crearAccion(
                solicitud,
                TipoAccionSolicitud
                        .ACEPTAR_ENVIO_INSPECCION,
                "Aceptar envío para inspección",
                "Debe aceptar el envío al depósito y la eventual devolución a su cargo."
        );

        return mapper.toDetalle(solicitud);
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO
    confirmarRecepcion(
            Long idSolicitud,
            ConfirmarRecepcionRequestDTO request
    ) {
        SolicitudPublicacion solicitud =
                obtenerSolicitud(idSolicitud);

        String ubicacion;

        if (request != null
                && request.getUbicacionActual() != null
                && !request.getUbicacionActual()
                .isBlank()) {

            ubicacion =
                    request.getUbicacionActual()
                            .trim();

        } else {
            ubicacion =
                    solicitud.getDireccionDeposito();
        }

        ejecutarDominio(() ->
                solicitud.confirmarRecepcion(
                        ubicacion
                )
        );

        return mapper.toDetalle(solicitud);
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO
    rechazarSolicitud(
            Long idSolicitud,
            RechazarSolicitudRequestDTO request
    ) {
        if (request == null
                || request.getMotivo() == null
                || request.getMotivo().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El motivo del rechazo es obligatorio"
            );
        }

        SolicitudPublicacion solicitud =
                obtenerSolicitud(idSolicitud);

        String motivo =
                request.getMotivo().trim();

        if (solicitud.getEstado()
                == EstadoSolicitud.PENDIENTE_REVISION
                || solicitud.getEstado()
                == EstadoSolicitud.INTERES_EMPRESA) {

            ejecutarDominio(() ->
                    solicitud.rechazarSinDevolucion(
                            motivo
                    )
            );

            return mapper.toDetalle(solicitud);
        }

        float costo =
                request.getCostoDevolucion() != null
                        ? request.getCostoDevolucion()
                        : 15000f;

        Moneda moneda =
                request.getMoneda() != null
                        ? request.getMoneda()
                        : Moneda.PESOS;

        DevolucionSolicitud devolucion =
                new DevolucionSolicitud(
                        solicitud,
                        costo,
                        moneda
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
                "Configure la dirección y el medio de pago para solicitar la devolución."
        );

        return mapper.toDetalle(solicitud);
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO
    proponerCondicionesVenta(
            Long idSolicitud,
            ProponerCondicionesVentaRequestDTO request
    ) {
        validarPropuesta(request);

        SolicitudPublicacion solicitud =
                obtenerSolicitud(idSolicitud);

        if (propuestaRepository.existsBySolicitud(
                solicitud
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud ya tiene una propuesta"
            );
        }

        Subasta subasta =
                subastaRepository
                        .findById(
                                request.getIdSubasta()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Subasta no encontrada"
                                )
                        );

        validarAnticipacionSubasta(subasta);

        PropuestaCondicionesVenta propuesta =
                new PropuestaCondicionesVenta(
                        solicitud,
                        subasta,
                        request.getPrecioBase(),
                        request.getPorcentajeComision()
                );

        propuestaRepository.save(propuesta);

        ejecutarDominio(() ->
                solicitud.proponerCondiciones(
                        propuesta
                )
        );

        crearAccion(
                solicitud,
                TipoAccionSolicitud
                        .ACEPTAR_CONDICIONES_VENTA,
                "Aceptar condiciones de venta",
                "Revise la subasta, el precio base y la comisión propuesta."
        );

        return mapper.toDetalle(solicitud);
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO incorporarASubasta(
            Long idSolicitud
    ) {
        SolicitudPublicacion solicitud =
                obtenerSolicitud(idSolicitud);

        PropuestaCondicionesVenta propuesta =
                propuestaRepository
                        .findBySolicitud(solicitud)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "No existe una propuesta"
                                )
                        );

        if (propuesta.getEstado()
                != EstadoPropuestaVenta.ACEPTADA) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario todavía no aceptó las condiciones"
            );
        }

        if (solicitud.getItem() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud ya fue incorporada"
            );
        }

        Catalogo catalogo =
                propuesta
                        .getSubasta()
                        .getCatalogo();

        if (catalogo == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La subasta no tiene catálogo"
            );
        }

        Cliente cliente =
                solicitud.getCliente();

        Empleado empleado =
                obtenerEmpleadoDisponible();

        Duenio duenio =
                obtenerOCrearDuenio(
                        cliente,
                        empleado
                );

        Usuario usuario =
                cliente
                        .getPersona()
                        .getUsuario();

        Item item =
                new Item(
                        solicitud.getTitulo(),
                        solicitud.getDescripcion(),
                        solicitud.getPrimeraImagen(),
                        solicitud.getImagenesUrl(),
                        solicitud.getCategoria(),
                        usuario
                );

        /*
         * Campos obligatorios de productos legacy.
         */
        item.setDuenio(duenio);
        item.setRevisor(empleado);

        item.setDescripcionCompleta(
                solicitud.getDescripcion()
        );

        item.setSolicitudPublicacion(
                solicitud
        );

        Item itemGuardado =
                itemRepository.save(item);

        ejecutarDominio(() ->
                solicitud.condicionesAceptadas(
                        itemGuardado
                )
        );

        EstadoItemCatalogoEntity estadoPendiente =
                estadoItemCatalogoRepository
                        .findByNombre(
                                EstadoItemCatalogo.PENDIENTE
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.CONFLICT,
                                        "No existe el estado PENDIENTE de lote"
                                )
                        );

        ItemCatalogo itemCatalogo =
                new ItemCatalogo(
                        itemGuardado,
                        propuesta.getPrecioBase(),
                        propuesta.getPorcentajeComision()
                );

        itemCatalogo.setCatalogo(catalogo);

        itemCatalogo.setEstadoEntity(
                estadoPendiente
        );

        itemCatalogoRepository.save(
                itemCatalogo
        );

        return mapper.toDetalle(solicitud);
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO actualizarUbicacion(
            Long idSolicitud,
            ActualizarUbicacionRequestDTO request
    ) {
        if (request == null
                || request.getUbicacionActual() == null
                || request.getUbicacionActual()
                .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La ubicación es obligatoria"
            );
        }

        SolicitudPublicacion solicitud =
                obtenerSolicitud(idSolicitud);

        ejecutarDominio(() ->
                solicitud.actualizarUbicacion(
                        request.getUbicacionActual()
                                .trim()
                )
        );

        return mapper.toDetalle(solicitud);
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO
    marcarDevolucionEnviada(
            Long idSolicitud
    ) {
        SolicitudPublicacion solicitud =
                obtenerSolicitud(idSolicitud);

        DevolucionSolicitud devolucion =
                devolucionRepository
                        .findBySolicitud(solicitud)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No existe una devolución"
                                )
                        );

        ejecutarDominio(
                devolucion::marcarEnviada
        );

        return mapper.toDetalle(solicitud);
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO
    confirmarEntregaDevolucion(
            Long idSolicitud
    ) {
        SolicitudPublicacion solicitud =
                obtenerSolicitud(idSolicitud);

        DevolucionSolicitud devolucion =
                devolucionRepository
                        .findBySolicitud(solicitud)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No existe una devolución"
                                )
                        );

        ejecutarDominio(
                devolucion::marcarEntregada
        );

        ejecutarDominio(
                solicitud::marcarDevuelta
        );

        return mapper.toDetalle(solicitud);
    }

    private Duenio obtenerOCrearDuenio(
            Cliente cliente,
            Empleado verificador
    ) {
        return duenioRepository
                .findById(
                        cliente.getIdentificador()
                )
                .orElseGet(() ->
                        crearDuenio(
                                cliente,
                                verificador
                        )
                );
    }

    private Duenio crearDuenio(
            Cliente cliente,
            Empleado verificador
    ) {
        if (cliente.getPersona() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El cliente no tiene una persona asociada"
            );
        }

        Duenio nuevoDuenio =
                new Duenio(
                        cliente.getPersona(),
                        verificador
                );

        return duenioRepository.save(
                nuevoDuenio
        );
    }

    private Empleado obtenerEmpleadoDisponible() {
        return empleadoRepository
                .findAll()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "No existe un empleado disponible como revisor"
                        )
                );
    }

    private void validarPropuesta(
            ProponerCondicionesVentaRequestDTO request
    ) {
        if (request == null
                || request.getIdSubasta() == null
                || request.getPrecioBase() == null
                || request.getPrecioBase() <= 0
                || request.getPorcentajeComision() == null
                || request.getPorcentajeComision() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La subasta, el precio base y la comisión son obligatorios"
            );
        }
    }

    private void validarAnticipacionSubasta(
            Subasta subasta
    ) {
        if (subasta.getFechaInicio() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La subasta no tiene fecha de inicio"
            );
        }

        LocalDate fechaMinima =
                LocalDate.now().plusDays(10);

        if (subasta.getFechaInicio()
                .toLocalDate()
                .isBefore(fechaMinima)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La subasta debe tener al menos 10 días de anticipación"
            );
        }
    }

    private SolicitudPublicacion obtenerSolicitud(
            Long idSolicitud
    ) {
        return solicitudRepository
                .findById(idSolicitud)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Solicitud no encontrada"
                        )
                );
    }

    private void crearAccion(
            SolicitudPublicacion solicitud,
            TipoAccionSolicitud tipo,
            String titulo,
            String descripcion
    ) {
        if (accionRepository
                .existsBySolicitudAndTipoAndEstado(
                        solicitud,
                        tipo,
                        EstadoAccionSolicitud.PENDIENTE
                )) {

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
                solicitud.agregarAccion(
                        accion
                )
        );

        accionRepository.save(accion);
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