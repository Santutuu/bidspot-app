package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.poliza.AumentarPolizaRequestDTO;
import com.subastas.subastas_api.DTO.poliza.PolizaSolicitudResponseDTO;
import com.subastas.subastas_api.model.AccionSolicitudPublicacion;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.EstadoAccionSolicitud;
import com.subastas.subastas_api.model.EstadoPoliza;
import com.subastas.subastas_api.model.EstadoSolicitud;
import com.subastas.subastas_api.model.Item;
import com.subastas.subastas_api.model.Poliza;
import com.subastas.subastas_api.model.SolicitudPublicacion;
import com.subastas.subastas_api.model.TipoAccionSolicitud;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.AccionSolicitudPublicacionRepository;
import com.subastas.subastas_api.repository.PolizaRepository;
import com.subastas.subastas_api.repository.SolicitudPublicacionRepository;
import com.subastas.subastas_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PolizaSolicitudService {

    private final SolicitudPublicacionRepository
            solicitudRepository;

    private final AccionSolicitudPublicacionRepository
            accionRepository;

    private final UsuarioRepository
            usuarioRepository;

    private final PolizaRepository
            polizaRepository;

    public PolizaSolicitudService(
            SolicitudPublicacionRepository solicitudRepository,
            AccionSolicitudPublicacionRepository accionRepository,
            UsuarioRepository usuarioRepository,
            PolizaRepository polizaRepository
    ) {
        this.solicitudRepository =
                solicitudRepository;

        this.accionRepository =
                accionRepository;

        this.usuarioRepository =
                usuarioRepository;

        this.polizaRepository =
                polizaRepository;
    }

    @Transactional(readOnly = true)
    public PolizaSolicitudResponseDTO obtenerPoliza(
            Long idSolicitud,
            Authentication authentication
    ) {
        Cliente cliente =
                obtenerCliente(authentication);

        SolicitudPublicacion solicitud =
                obtenerSolicitudDelCliente(
                        idSolicitud,
                        cliente
                );

        Item item =
                obtenerItemDeSolicitud(solicitud);

        Poliza poliza =
                obtenerPolizaDeItem(item);

        return toDTO(
                solicitud,
                item,
                poliza
        );
    }

    @Transactional
    public PolizaSolicitudResponseDTO aumentarPoliza(
            Long idSolicitud,
            AumentarPolizaRequestDTO request,
            Authentication authentication
    ) {
        Cliente cliente =
                obtenerCliente(authentication);

        SolicitudPublicacion solicitud =
                obtenerSolicitudDelCliente(
                        idSolicitud,
                        cliente
                );

        validarSolicitudPendientePoliza(
                solicitud
        );

        Item item =
                obtenerItemDeSolicitud(solicitud);

        Poliza poliza =
                obtenerPolizaDeItem(item);

        validarAumentoPoliza(
                request,
                poliza
        );

        ejecutarDominio(() ->
                poliza.solicitarAumento(
                        request.getNuevoMontoAsegurado()
                )
        );

        Poliza guardada =
                polizaRepository.save(poliza);

        return toDTO(
                solicitud,
                item,
                guardada
        );
    }

    @Transactional
    public PolizaSolicitudResponseDTO aceptarPoliza(
            Long idSolicitud,
            Authentication authentication
    ) {
        Cliente cliente =
                obtenerCliente(authentication);

        SolicitudPublicacion solicitud =
                obtenerSolicitudDelCliente(
                        idSolicitud,
                        cliente
                );

        validarSolicitudPendientePoliza(
                solicitud
        );

        Item item =
                obtenerItemDeSolicitud(solicitud);

        Poliza poliza =
                obtenerPolizaDeItem(item);

        if (poliza.getEstado()
                == EstadoPoliza.ACEPTADA) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La póliza ya fue aceptada"
            );
        }

        if (poliza.getEstado()
                == EstadoPoliza.RECHAZADA) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede aceptar una póliza rechazada"
            );
        }

        ejecutarDominio(poliza::aceptar);

        polizaRepository.save(poliza);

        completarAccionRevisionPoliza(
                solicitud
        );

        ejecutarDominio(
                solicitud::marcarListaParaSubasta
        );

        solicitudRepository.save(solicitud);

        return toDTO(
                solicitud,
                item,
                poliza
        );
    }

    @Transactional
    public PolizaSolicitudResponseDTO rechazarPoliza(
            Long idSolicitud,
            Authentication authentication
    ) {
        Cliente cliente =
                obtenerCliente(authentication);

        SolicitudPublicacion solicitud =
                obtenerSolicitudDelCliente(
                        idSolicitud,
                        cliente
                );

        validarSolicitudPendientePoliza(
                solicitud
        );

        Item item =
                obtenerItemDeSolicitud(solicitud);

        Poliza poliza =
                obtenerPolizaDeItem(item);

        if (poliza.getEstado()
                == EstadoPoliza.ACEPTADA) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede rechazar una póliza aceptada"
            );
        }

        if (poliza.getEstado()
                == EstadoPoliza.RECHAZADA) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La póliza ya fue rechazada"
            );
        }

        ejecutarDominio(poliza::rechazar);

        polizaRepository.save(poliza);

        AccionSolicitudPublicacion accion =
                obtenerAccionRevisionPoliza(
                        solicitud
                );

        ejecutarDominio(() ->
                accion.completar(
                        false,
                        "El usuario rechazó la póliza propuesta"
                )
        );

        accionRepository.save(accion);

        return toDTO(
                solicitud,
                item,
                poliza
        );
    }

    private Cliente obtenerCliente(
            Authentication authentication
    ) {
        if (authentication == null
                || authentication.getName() == null
                || authentication.getName()
                .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        Usuario usuario =
                usuarioRepository
                        .findByMail(
                                authentication.getName()
                                        .trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Usuario no autenticado"
                                )
                        );

        if (usuario.estaBloqueado()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "La cuenta se encuentra bloqueada"
            );
        }

        Cliente cliente =
                usuario.getCliente();

        if (cliente == null
                || !usuario
                .estaValidadoComoCliente()) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no está validado como cliente"
            );
        }

        return cliente;
    }

    private SolicitudPublicacion
    obtenerSolicitudDelCliente(
            Long idSolicitud,
            Cliente cliente
    ) {
        if (idSolicitud == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El id de la solicitud es obligatorio"
            );
        }

        return solicitudRepository
                .findByIdSolicitudAndCliente(
                        idSolicitud,
                        cliente
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Solicitud no encontrada para el cliente autenticado"
                        )
                );
    }

    private Item obtenerItemDeSolicitud(
            SolicitudPublicacion solicitud
    ) {
        if (solicitud.getItem() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Todavía no existe un producto asociado a esta solicitud"
            );
        }

        return solicitud.getItem();
    }

    private Poliza obtenerPolizaDeItem(
            Item item
    ) {
        if (item.getPoliza() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Todavía no existe una póliza asociada al producto"
            );
        }

        return item.getPoliza();
    }

    private void validarSolicitudPendientePoliza(
            SolicitudPublicacion solicitud
    ) {
        if (solicitud.getEstado()
                != EstadoSolicitud.PENDIENTE_POLIZA) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud no se encuentra pendiente de póliza"
            );
        }
    }

    private void validarAumentoPoliza(
            AumentarPolizaRequestDTO request,
            Poliza poliza
    ) {
        if (request == null
                || request.getNuevoMontoAsegurado()
                == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nuevo monto asegurado es obligatorio"
            );
        }

        Float nuevoMonto =
                request.getNuevoMontoAsegurado();

        if (nuevoMonto <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nuevo monto asegurado debe ser mayor a cero"
            );
        }

        if (poliza.getMontoAsegurado() != null
                && nuevoMonto
                <= poliza.getMontoAsegurado()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nuevo monto asegurado debe ser mayor al monto actual"
            );
        }

        if (poliza.getEstado()
                == EstadoPoliza.ACEPTADA) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede modificar una póliza aceptada"
            );
        }

        if (poliza.getEstado()
                == EstadoPoliza.RECHAZADA) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede modificar una póliza rechazada"
            );
        }
    }

    private AccionSolicitudPublicacion
    obtenerAccionRevisionPoliza(
            SolicitudPublicacion solicitud
    ) {
        return accionRepository
                .findFirstBySolicitudAndTipoAndEstadoOrderByFechaCreacionDesc(
                        solicitud,
                        TipoAccionSolicitud.REVISAR_POLIZA,
                        EstadoAccionSolicitud.PENDIENTE
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "No existe una acción pendiente para revisar la póliza"
                        )
                );
    }

    private void completarAccionRevisionPoliza(
            SolicitudPublicacion solicitud
    ) {
        AccionSolicitudPublicacion accion =
                obtenerAccionRevisionPoliza(
                        solicitud
                );

        ejecutarDominio(() ->
                accion.completar(
                        true,
                        "El usuario aceptó la póliza propuesta"
                )
        );

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