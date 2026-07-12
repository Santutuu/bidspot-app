package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.publicacion.ResponderAccionRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.RespuestaAccionDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionDetalleDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionResumenDTO;
import com.subastas.subastas_api.model.AccionRequerida;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.EstadoItem;
import com.subastas.subastas_api.model.EstadoSolicitud;
import com.subastas.subastas_api.model.RespuestaAccionRequerida;
import com.subastas.subastas_api.model.SolicitudPublicacion;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.RespuestaAccionRequeridaRepository;
import com.subastas.subastas_api.repository.SolicitudPublicacionRepository;
import com.subastas.subastas_api.repository.SubastaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SolicitudPublicacionService {

    private final SolicitudPublicacionRepository solicitudRepository;
    private final RespuestaAccionRequeridaRepository respuestaRepository;
    private final SubastaRepository subastaRepository;

    public SolicitudPublicacionService(
            SolicitudPublicacionRepository solicitudRepository,
            RespuestaAccionRequeridaRepository respuestaRepository,
            SubastaRepository subastaRepository
    ) {
        this.solicitudRepository = solicitudRepository;
        this.respuestaRepository = respuestaRepository;
        this.subastaRepository = subastaRepository;
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO crearSolicitud(
            Usuario usuario,
            SolicitudPublicacionRequestDTO request
    ) {
        Cliente cliente = obtenerClienteValido(usuario);

        validarRequest(request);

        SolicitudPublicacion solicitud =
                new SolicitudPublicacion(
                        cliente,
                        request.getCategoria(),
                        request.getTitulo().trim(),
                        request.getDescripcion().trim(),
                        request.getImagenesUrl(),
                        request.isDeclaracionPropiedad()
                );

        SolicitudPublicacion guardada =
                solicitudRepository.save(solicitud);

        return toDetalleDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<SolicitudPublicacionResumenDTO>
    listarMisSolicitudes(
            Usuario usuario
    ) {
        Cliente cliente = obtenerClienteValido(usuario);

        return solicitudRepository
                .findByClienteOrderByFechaCreacionDesc(cliente)
                .stream()
                .map(this::toResumenDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public SolicitudPublicacionDetalleDTO obtenerDetalle(
            Long idSolicitud,
            Usuario usuario
    ) {
        Cliente cliente = obtenerClienteValido(usuario);

        return toDetalleDTO(
                obtenerSolicitudDelCliente(
                        idSolicitud,
                        cliente
                )
        );
    }

    @Transactional
    public void cancelarSolicitud(
            Long idSolicitud,
            Usuario usuario
    ) {
        Cliente cliente = obtenerClienteValido(usuario);

        SolicitudPublicacion solicitud =
                obtenerSolicitudDelCliente(
                        idSolicitud,
                        cliente
                );

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE
                && solicitud.getEstado()
                != EstadoSolicitud.EN_REVISION) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud ya no puede cancelarse"
            );
        }

        solicitud.setEstado(EstadoSolicitud.CANCELADA);

        solicitudRepository.save(solicitud);
    }

    @Transactional
    public SolicitudPublicacionDetalleDTO responderAccion(
            Long idSolicitud,
            AccionRequerida accion,
            Usuario usuario,
            ResponderAccionRequestDTO request
    ) {
        Cliente cliente = obtenerClienteValido(usuario);

        SolicitudPublicacion solicitud =
                obtenerSolicitudDelCliente(
                        idSolicitud,
                        cliente
                );

        if (!solicitud
                .getAccionesRequeridas()
                .contains(accion)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La acción indicada no está requerida para esta solicitud"
            );
        }

        validarRespuestaAccion(
                accion,
                request
        );

        RespuestaAccionRequerida respuesta =
                new RespuestaAccionRequerida(
                        solicitud,
                        accion,
                        request.getTipoRespuesta(),
                        request.getAceptada(),
                        request.getComentario(),
                        request.getArchivoUrl(),
                        request.getMontoAseguradoSolicitado()
                );

        respuestaRepository.save(respuesta);

        solicitud.eliminarAccionRequerida(accion);

        aplicarEfectoDeRespuesta(
                solicitud,
                accion,
                request
        );

        SolicitudPublicacion guardada =
                solicitudRepository.save(solicitud);

        return toDetalleDTO(guardada);
    }

    private Cliente obtenerClienteValido(
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

        Cliente cliente = usuario.getCliente();

        if (cliente == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario no tiene un perfil de cliente asociado"
            );
        }

        if (!usuario.estaValidadoComoCliente()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario todavía no fue validado como cliente"
            );
        }

        return cliente;
    }

    private SolicitudPublicacion obtenerSolicitudDelCliente(
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
                                "No existe una solicitud con ese id para este cliente"
                        )
                );
    }

    private void aplicarEfectoDeRespuesta(
            SolicitudPublicacion solicitud,
            AccionRequerida accion,
            ResponderAccionRequestDTO request
    ) {
        if (accion
                == AccionRequerida.ACEPTAR_CONDICIONES_VENTA
                && Boolean.FALSE.equals(
                request.getAceptada()
        )) {
            solicitud.setEstado(
                    EstadoSolicitud.CANCELADA
            );

            if (solicitud.getItem() != null) {
                solicitud.getItem().setEstado(
                        EstadoItem.RECHAZADO
                );
            }
        }

        if (accion == AccionRequerida.ACEPTAR_POLIZA
                && solicitud.getItem() != null
                && solicitud.getItem().getPoliza() != null) {

            if (Boolean.TRUE.equals(
                    request.getAceptada()
            )) {
                solicitud.getItem()
                        .getPoliza()
                        .aceptar();

            } else if (Boolean.FALSE.equals(
                    request.getAceptada()
            )) {
                solicitud.getItem()
                        .getPoliza()
                        .rechazar();
            }
        }

        if (accion == AccionRequerida.MODIFICAR_POLIZA
                && solicitud.getItem() != null
                && solicitud.getItem().getPoliza() != null) {

            solicitud.getItem()
                    .getPoliza()
                    .solicitarAumento(
                            request.getMontoAseguradoSolicitado()
                    );

            solicitud.agregarAccionRequerida(
                    AccionRequerida.ACEPTAR_POLIZA
            );
        }
    }

    private void validarRequest(
            SolicitudPublicacionRequestDTO request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de la solicitud son obligatorios"
            );
        }

        if (request.getCategoria() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La categoría es obligatoria"
            );
        }

        if (request.getTitulo() == null
                || request.getTitulo()
                .trim()
                .length() < 3) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El título es obligatorio"
            );
        }

        if (request.getDescripcion() == null
                || request.getDescripcion()
                .trim()
                .length() < 10) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripción es obligatoria"
            );
        }

        if (request.getImagenesUrl() == null
                || request.getImagenesUrl().size() < 6) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe cargar al menos 6 imágenes"
            );
        }

        if (!request.isDeclaracionPropiedad()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe declarar que el bien le pertenece"
            );
        }
    }

    private void validarRespuestaAccion(
            AccionRequerida accion,
            ResponderAccionRequestDTO request
    ) {
        if (accion == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La acción es obligatoria"
            );
        }

        if (request == null
                || request.getTipoRespuesta() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de respuesta es obligatorio"
            );
        }

        switch (accion) {
            case ACEPTAR_CONDICIONES_VENTA,
                 ACEPTAR_POLIZA,
                 PROPUESTA_COLECCION -> {

                if (request.getAceptada() == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Debe aceptar o rechazar la propuesta"
                    );
                }
            }

            case MODIFICAR_POLIZA -> {
                if (request.getMontoAseguradoSolicitado() == null
                        || request
                        .getMontoAseguradoSolicitado() <= 0) {

                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Debe indicar un monto asegurado válido"
                    );
                }
            }

            case COMPROBAR_ORIGEN_LICITO -> {
                if (request.getArchivoUrl() == null
                        || request.getArchivoUrl().isBlank()) {

                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Debe adjuntar documentación del origen lícito"
                    );
                }
            }

            case ENVIAR_ITEM -> {
                if (request.getComentario() == null
                        || request.getComentario().isBlank()) {

                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Debe indicar cómo o cuándo enviará el item"
                    );
                }
            }
        }
    }

    private SolicitudPublicacionResumenDTO toResumenDTO(
            SolicitudPublicacion solicitud
    ) {
        Subasta subasta =
                obtenerSubastaAsignada(solicitud);

        return new SolicitudPublicacionResumenDTO(
                solicitud.getIdSolicitud(),
                solicitud.getTitulo(),
                solicitud.getEstado().name(),
                solicitud.getCategoria().name(),
                solicitud.getPrimeraImagen(),
                subasta != null
                        ? subasta.getIdSubasta()
                        : null,
                subasta != null
                        ? subasta.getFechaInicio()
                        : null
        );
    }

    private SolicitudPublicacionDetalleDTO toDetalleDTO(
            SolicitudPublicacion solicitud
    ) {
        Subasta subasta =
                obtenerSubastaAsignada(solicitud);

        List<RespuestaAccionDTO> respuestas =
                solicitud.getRespuestasAcciones()
                        .stream()
                        .map(this::toRespuestaDTO)
                        .toList();

        return new SolicitudPublicacionDetalleDTO(
                solicitud.getIdSolicitud(),
                solicitud.getTitulo(),
                solicitud.getDescripcion(),
                solicitud.getCategoria().name(),
                solicitud.getEstado().name(),
                solicitud.getImagenesUrl(),
                solicitud.isDeclaracionPropiedad(),
                solicitud.getAccionesRequeridas(),
                respuestas,
                solicitud.getMotivoRechazo(),
                solicitud.getUbicacionDeposito(),
                subasta != null
                        ? subasta.getIdSubasta()
                        : null,
                subasta != null
                        && subasta.getCatalogo() != null
                        ? subasta.getCatalogo()
                        .getDescripcion()
                        : null,
                subasta != null
                        ? subasta.getFechaInicio()
                        : null,
                subasta != null
                        ? subasta.getUbicacion()
                        : null
        );
    }

    private RespuestaAccionDTO toRespuestaDTO(
            RespuestaAccionRequerida respuesta
    ) {
        return new RespuestaAccionDTO(
                respuesta.getIdRespuesta(),
                respuesta.getAccion().name(),
                respuesta.getTipoRespuesta().name(),
                respuesta.getAceptada(),
                respuesta.getComentario(),
                respuesta.getArchivoUrl(),
                respuesta.getMontoAseguradoSolicitado(),
                respuesta.getFechaRespuesta()
        );
    }

    private Subasta obtenerSubastaAsignada(
            SolicitudPublicacion solicitud
    ) {
        if (solicitud.getItem() == null) {
            return null;
        }

        return subastaRepository.findByItemIdItem(
                solicitud.getItem().getIdItem()
        );
    }
}