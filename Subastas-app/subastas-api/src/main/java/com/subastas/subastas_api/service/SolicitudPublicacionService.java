package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.publicacion.*;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.RespuestaAccionRequeridaRepository;
import com.subastas.subastas_api.repository.SolicitudPublicacionRepository;
import com.subastas.subastas_api.repository.SubastaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SolicitudPublicacionService {

    private final SolicitudPublicacionRepository solicitudRepository;
    private final RespuestaAccionRequeridaRepository respuestaRepository;
    private final SubastaRepository subastaRepository;

    public SolicitudPublicacionService(SolicitudPublicacionRepository solicitudRepository,
                                       RespuestaAccionRequeridaRepository respuestaRepository,
                                       SubastaRepository subastaRepository) {
        this.solicitudRepository = solicitudRepository;
        this.respuestaRepository = respuestaRepository;
        this.subastaRepository = subastaRepository;
    }

    public SolicitudPublicacionDetalleDTO crearSolicitud(Usuario usuario,
                                                         SolicitudPublicacionRequestDTO request) {
        validarRequest(request);

        SolicitudPublicacion solicitud = new SolicitudPublicacion(
                usuario,
                request.getCategoria(),
                request.getTitulo().trim(),
                request.getDescripcion().trim(),
                request.getImagenesUrl(),
                request.isDeclaracionPropiedad()
        );

        return toDetalleDTO(solicitudRepository.save(solicitud));
    }

    public List<SolicitudPublicacionResumenDTO> listarMisSolicitudes(Usuario usuario) {
        return solicitudRepository.findByUsuarioOrderByFechaCreacionDesc(usuario)
                .stream()
                .map(this::toResumenDTO)
                .toList();
    }

    public SolicitudPublicacionDetalleDTO obtenerDetalle(Long idSolicitud, Usuario usuario) {
        return toDetalleDTO(obtenerSolicitudDelUsuario(idSolicitud, usuario));
    }

    public void cancelarSolicitud(Long idSolicitud, Usuario usuario) {
        SolicitudPublicacion solicitud = obtenerSolicitudDelUsuario(idSolicitud, usuario);

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE &&
                solicitud.getEstado() != EstadoSolicitud.EN_REVISION) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud ya no puede cancelarse"
            );
        }

        solicitud.setEstado(EstadoSolicitud.CANCELADA);
        solicitudRepository.save(solicitud);
    }

    public SolicitudPublicacionDetalleDTO responderAccion(Long idSolicitud,
                                                          AccionRequerida accion,
                                                          Usuario usuario,
                                                          ResponderAccionRequestDTO request) {
        SolicitudPublicacion solicitud = obtenerSolicitudDelUsuario(idSolicitud, usuario);

        if (!solicitud.getAccionesRequeridas().contains(accion)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La acción indicada no está requerida para esta solicitud"
            );
        }

        validarRespuestaAccion(accion, request);

        RespuestaAccionRequerida respuesta = new RespuestaAccionRequerida(
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

        aplicarEfectoDeRespuesta(solicitud, accion, request);

        solicitudRepository.save(solicitud);

        return toDetalleDTO(solicitud);
    }

    private void aplicarEfectoDeRespuesta(SolicitudPublicacion solicitud,
                                          AccionRequerida accion,
                                          ResponderAccionRequestDTO request) {
        if (accion == AccionRequerida.ACEPTAR_CONDICIONES_VENTA &&
                Boolean.FALSE.equals(request.getAceptada())) {
            solicitud.setEstado(EstadoSolicitud.CANCELADA);

            if (solicitud.getItem() != null) {
                solicitud.getItem().setEstado(EstadoItem.RECHAZADO);
            }
        }

        if (accion == AccionRequerida.ACEPTAR_POLIZA &&
                solicitud.getItem() != null &&
                solicitud.getItem().getPoliza() != null) {

            if (Boolean.TRUE.equals(request.getAceptada())) {
                solicitud.getItem().getPoliza().aceptar();
            } else if (Boolean.FALSE.equals(request.getAceptada())) {
                solicitud.getItem().getPoliza().rechazar();
            }
        }

        if (accion == AccionRequerida.MODIFICAR_POLIZA &&
                solicitud.getItem() != null &&
                solicitud.getItem().getPoliza() != null) {

            solicitud.getItem()
                    .getPoliza()
                    .solicitarAumento(request.getMontoAseguradoSolicitado());

            solicitud.agregarAccionRequerida(AccionRequerida.ACEPTAR_POLIZA);
        }
    }

    private SolicitudPublicacion obtenerSolicitudDelUsuario(Long idSolicitud, Usuario usuario) {
        return solicitudRepository.findByIdSolicitudAndUsuario(idSolicitud, usuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una solicitud con ese id"
                ));
    }

    private void validarRequest(SolicitudPublicacionRequestDTO request) {
        if (request.getCategoria() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La categoría es obligatoria");
        }

        if (request.getTitulo() == null || request.getTitulo().trim().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El título es obligatorio");
        }

        if (request.getDescripcion() == null || request.getDescripcion().trim().length() < 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La descripción es obligatoria");
        }

        if (request.getImagenesUrl() == null || request.getImagenesUrl().size() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe cargar al menos 6 imágenes");
        }

        if (!request.isDeclaracionPropiedad()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe declarar que el bien le pertenece"
            );
        }
    }

    private void validarRespuestaAccion(AccionRequerida accion,
                                        ResponderAccionRequestDTO request) {
        if (request.getTipoRespuesta() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de respuesta es obligatorio");
        }

        switch (accion) {
            case ACEPTAR_CONDICIONES_VENTA, ACEPTAR_POLIZA, PROPUESTA_COLECCION -> {
                if (request.getAceptada() == null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Debe aceptar o rechazar la propuesta"
                    );
                }
            }

            case MODIFICAR_POLIZA -> {
                if (request.getMontoAseguradoSolicitado() == null ||
                        request.getMontoAseguradoSolicitado() <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Debe indicar un monto asegurado válido"
                    );
                }
            }

            case COMPROBAR_ORIGEN_LICITO -> {
                if (request.getArchivoUrl() == null || request.getArchivoUrl().isBlank()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Debe adjuntar documentación del origen lícito"
                    );
                }
            }

            case ENVIAR_ITEM -> {
                if (request.getComentario() == null || request.getComentario().isBlank()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Debe indicar cómo o cuándo enviará el item"
                    );
                }
            }
        }
    }

    private SolicitudPublicacionResumenDTO toResumenDTO(SolicitudPublicacion solicitud) {
        Subasta subasta = obtenerSubastaAsignada(solicitud);

        return new SolicitudPublicacionResumenDTO(
                solicitud.getIdSolicitud(),
                solicitud.getTitulo(),
                solicitud.getEstado().name(),
                solicitud.getCategoria().name(),
                solicitud.getPrimeraImagen(),
                subasta != null ? subasta.getIdSubasta() : null,
                subasta != null ? subasta.getFechaInicio() : null
        );
    }

    private SolicitudPublicacionDetalleDTO toDetalleDTO(SolicitudPublicacion solicitud) {
        Subasta subasta = obtenerSubastaAsignada(solicitud);

        List<RespuestaAccionDTO> respuestas = solicitud.getRespuestasAcciones()
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
                subasta != null ? subasta.getIdSubasta() : null,
                subasta != null && subasta.getCatalogo() != null ? subasta.getCatalogo().getDescripcion() : null,
                subasta != null ? subasta.getFechaInicio() : null,
                subasta != null ? subasta.getUbicacion() : null
        );
    }

    private RespuestaAccionDTO toRespuestaDTO(RespuestaAccionRequerida respuesta) {
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

    private Subasta obtenerSubastaAsignada(SolicitudPublicacion solicitud) {
        if (solicitud.getItem() == null) {
            return null;
        }

        return subastaRepository.findByItemIdItem(solicitud.getItem().getIdItem());
    }
}