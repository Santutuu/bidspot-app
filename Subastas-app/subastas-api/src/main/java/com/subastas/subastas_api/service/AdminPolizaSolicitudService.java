package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.poliza.CrearPolizaSolicitudRequestDTO;
import com.subastas.subastas_api.DTO.poliza.PolizaSolicitudResponseDTO;
import com.subastas.subastas_api.model.AccionSolicitudPublicacion;
import com.subastas.subastas_api.model.EstadoAccionSolicitud;
import com.subastas.subastas_api.model.EstadoSolicitud;
import com.subastas.subastas_api.model.Item;
import com.subastas.subastas_api.model.Poliza;
import com.subastas.subastas_api.model.PropuestaCondicionesVenta;
import com.subastas.subastas_api.model.SolicitudPublicacion;
import com.subastas.subastas_api.model.TipoAccionSolicitud;
import com.subastas.subastas_api.repository.AccionSolicitudPublicacionRepository;
import com.subastas.subastas_api.repository.ItemRepository;
import com.subastas.subastas_api.repository.PolizaRepository;
import com.subastas.subastas_api.repository.SolicitudPublicacionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminPolizaSolicitudService {

    private final SolicitudPublicacionRepository
            solicitudRepository;

    private final PolizaRepository
            polizaRepository;

    private final ItemRepository
            itemRepository;

    private final AccionSolicitudPublicacionRepository
            accionRepository;

    public AdminPolizaSolicitudService(
            SolicitudPublicacionRepository solicitudRepository,
            PolizaRepository polizaRepository,
            ItemRepository itemRepository,
            AccionSolicitudPublicacionRepository accionRepository
    ) {
        this.solicitudRepository =
                solicitudRepository;

        this.polizaRepository =
                polizaRepository;

        this.itemRepository =
                itemRepository;

        this.accionRepository =
                accionRepository;
    }

    /*
     * La empresa contrata una póliza para el producto
     * creado desde la solicitud.
     *
     * La solicitud ya debe haber sido incorporada a una
     * subasta y encontrarse en PENDIENTE_POLIZA.
     */
    @Transactional
    public PolizaSolicitudResponseDTO crearPoliza(
            Long idSolicitud,
            CrearPolizaSolicitudRequestDTO request
    ) {
        validarRequest(request);

        SolicitudPublicacion solicitud =
                obtenerSolicitud(idSolicitud);

        validarEstadoSolicitud(solicitud);

        Item item =
                obtenerProducto(solicitud);

        if (item.getPoliza() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El producto ya tiene una póliza asociada"
            );
        }

        String numeroPoliza =
                request.getNumeroPoliza()
                        .trim();

        if (polizaRepository.existsByNroPoliza(
                numeroPoliza
        )) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una póliza con el número "
                            + numeroPoliza
            );
        }

        PropuestaCondicionesVenta propuesta =
                solicitud.getPropuestaVenta();

        if (propuesta == null
                || propuesta.getPrecioBase() == null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud no tiene condiciones de venta válidas"
            );
        }

        Float precioBase =
                propuesta.getPrecioBase();

        Float montoAsegurado =
                request.getMontoAsegurado();

        Float tasaSeguro =
                request.getTasaSeguro();

        Float premio =
                Poliza.calcularPremio(
                        montoAsegurado,
                        tasaSeguro
                );

        Poliza poliza =
                new Poliza(
                        numeroPoliza,
                        montoAsegurado,
                        premio,
                        precioBase,
                        tasaSeguro,
                        request.getCompania()
                                .trim()
                );

        /*
         * Se guarda primero porque Item no tiene cascade PERSIST
         * hacia Poliza.
         */
        Poliza polizaGuardada =
                polizaRepository.save(poliza);

        item.setPoliza(polizaGuardada);

        itemRepository.save(item);

        crearAccionRevisionPoliza(solicitud);

        return toDTO(
                solicitud,
                item,
                polizaGuardada
        );
    }

    @Transactional(readOnly = true)
    public PolizaSolicitudResponseDTO obtenerPoliza(
            Long idSolicitud
    ) {
        SolicitudPublicacion solicitud =
                obtenerSolicitud(idSolicitud);

        Item item =
                obtenerProducto(solicitud);

        if (item.getPoliza() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "El producto todavía no tiene una póliza asociada"
            );
        }

        return toDTO(
                solicitud,
                item,
                item.getPoliza()
        );
    }

    private SolicitudPublicacion obtenerSolicitud(
            Long idSolicitud
    ) {
        if (idSolicitud == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El id de la solicitud es obligatorio"
            );
        }

        return solicitudRepository
                .findById(idSolicitud)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Solicitud no encontrada"
                        )
                );
    }

    private Item obtenerProducto(
            SolicitudPublicacion solicitud
    ) {
        if (solicitud.getItem() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud todavía no fue incorporada a una subasta"
            );
        }

        return solicitud.getItem();
    }

    private void validarEstadoSolicitud(
            SolicitudPublicacion solicitud
    ) {
        if (solicitud.getEstado()
                != EstadoSolicitud.PENDIENTE_POLIZA) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud debe encontrarse en estado PENDIENTE_POLIZA"
            );
        }
    }

    private void validarRequest(
            CrearPolizaSolicitudRequestDTO request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de la póliza son obligatorios"
            );
        }

        if (request.getNumeroPoliza() == null
                || request.getNumeroPoliza()
                .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El número de póliza es obligatorio"
            );
        }

        if (request.getCompania() == null
                || request.getCompania()
                .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La compañía aseguradora es obligatoria"
            );
        }

        if (request.getMontoAsegurado() == null
                || request.getMontoAsegurado() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El monto asegurado debe ser mayor a cero"
            );
        }

        if (request.getTasaSeguro() == null
                || request.getTasaSeguro() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La tasa del seguro debe ser mayor a cero"
            );
        }

        if (request.getTasaSeguro() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La tasa del seguro no puede superar el 100 %"
            );
        }
    }

    private void crearAccionRevisionPoliza(
            SolicitudPublicacion solicitud
    ) {
        boolean yaExiste =
                accionRepository
                        .existsBySolicitudAndTipoAndEstado(
                                solicitud,
                                TipoAccionSolicitud.REVISAR_POLIZA,
                                EstadoAccionSolicitud.PENDIENTE
                        );

        if (yaExiste) {
            return;
        }

        AccionSolicitudPublicacion accion =
                new AccionSolicitudPublicacion(
                        solicitud,
                        TipoAccionSolicitud.REVISAR_POLIZA,
                        "Revisar póliza",
                        "Revise el monto asegurado, el premio y la compañía propuesta."
                );

        try {
            solicitud.agregarAccion(accion);
        } catch (IllegalStateException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    exception.getMessage()
            );
        }

        accionRepository.save(accion);
    }

    private PolizaSolicitudResponseDTO toDTO(
            SolicitudPublicacion solicitud,
            Item item,
            Poliza poliza
    ) {
        return new PolizaSolicitudResponseDTO(
                solicitud.getIdSolicitud(),
                item.getIdItem(),
                item.getTitulo(),
                poliza.getIdPoliza(),
                poliza.getNroPoliza(),
                poliza.getMontoAsegurado(),
                poliza.getPremio(),
                poliza.getPrecioBase(),
                poliza.getTasaSeguro(),
                poliza.getCompania(),
                poliza.getEstado()
        );
    }
}