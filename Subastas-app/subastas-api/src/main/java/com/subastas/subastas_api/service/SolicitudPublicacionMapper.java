package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.publicacion.AccionSolicitudDTO;
import com.subastas.subastas_api.DTO.publicacion.DevolucionSolicitudDTO;
import com.subastas.subastas_api.DTO.publicacion.PropuestaCondicionesVentaDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionDetalleDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionResumenDTO;
import com.subastas.subastas_api.model.AccionSolicitudPublicacion;
import com.subastas.subastas_api.model.DevolucionSolicitud;
import com.subastas.subastas_api.model.EstadoAccionSolicitud;
import com.subastas.subastas_api.model.PropuestaCondicionesVenta;
import com.subastas.subastas_api.model.SolicitudPublicacion;
import com.subastas.subastas_api.model.Subasta;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SolicitudPublicacionMapper {

    public SolicitudPublicacionResumenDTO toResumen(
            SolicitudPublicacion solicitud
    ) {
        PropuestaCondicionesVenta propuesta =
                solicitud.getPropuestaVenta();

        Subasta subasta = propuesta != null
                ? propuesta.getSubasta()
                : null;

        int cantidadPendientes = (int) solicitud
                .getAcciones()
                .stream()
                .filter(AccionSolicitudPublicacion::estaPendiente)
                .count();

        return new SolicitudPublicacionResumenDTO(
                solicitud.getIdSolicitud(),
                solicitud.getTitulo(),
                solicitud.getEstado().name(),
                solicitud.getCategoria().name(),
                solicitud.getPrimeraImagen(),
                solicitud.getFechaCreacion(),
                cantidadPendientes,
                subasta != null
                        ? subasta.getIdSubasta()
                        : null,
                subasta != null
                        ? subasta.getFechaInicio()
                        : null
        );
    }

    public SolicitudPublicacionDetalleDTO toDetalle(
            SolicitudPublicacion solicitud
    ) {
        List<AccionSolicitudDTO> pendientes =
                solicitud.getAcciones()
                        .stream()
                        .filter(
                                AccionSolicitudPublicacion::estaPendiente
                        )
                        .map(this::toAccion)
                        .toList();

        List<AccionSolicitudDTO> completadas =
                solicitud.getAcciones()
                        .stream()
                        .filter(accion ->
                                accion.getEstado()
                                        == EstadoAccionSolicitud.COMPLETADA
                        )
                        .map(this::toAccion)
                        .toList();

        PropuestaCondicionesVenta propuesta =
                solicitud.getPropuestaVenta();

        Subasta subasta = propuesta != null
                ? propuesta.getSubasta()
                : null;

        return new SolicitudPublicacionDetalleDTO(
                solicitud.getIdSolicitud(),
                solicitud.getTitulo(),
                solicitud.getDescripcion(),
                solicitud.getCategoria().name(),
                solicitud.getEstado().name(),
                solicitud.getImagenesUrl(),
                solicitud.isDeclaracionPropiedad(),
                solicitud.getDireccionDeposito(),
                solicitud.getFechaLimiteEnvio(),
                solicitud.getAceptaDevolucionConCargo(),
                solicitud.getFechaRecepcion(),
                solicitud.getUbicacionActual(),
                solicitud.getFechaActualizacionUbicacion(),
                solicitud.getMotivoRechazo(),
                pendientes,
                completadas,
                propuesta != null
                        ? toPropuesta(propuesta)
                        : null,
                solicitud.getDevolucion() != null
                        ? toDevolucion(
                        solicitud.getDevolucion()
                )
                        : null,
                solicitud.getItem() != null
                        ? solicitud.getItem().getIdItem()
                        : null,
                subasta != null
                        ? subasta.getIdSubasta()
                        : null,
                obtenerTituloSubasta(subasta),
                subasta != null
                        ? subasta.getFechaInicio()
                        : null,
                subasta != null
                        ? subasta.getUbicacion()
                        : null,
                solicitud.getFechaCreacion(),
                solicitud.getFechaActualizacion()
        );
    }

    private AccionSolicitudDTO toAccion(
            AccionSolicitudPublicacion accion
    ) {
        return new AccionSolicitudDTO(
                accion.getIdAccion(),
                accion.getTipo().name(),
                accion.getEstado().name(),
                accion.getTitulo(),
                accion.getDescripcion(),
                accion.getAceptada(),
                accion.getComentarioRespuesta(),
                accion.getArchivoUrl(),
                accion.getFechaCreacion(),
                accion.getFechaResolucion()
        );
    }

    private PropuestaCondicionesVentaDTO toPropuesta(
            PropuestaCondicionesVenta propuesta
    ) {
        Subasta subasta = propuesta.getSubasta();

        return new PropuestaCondicionesVentaDTO(
                propuesta.getIdPropuesta(),
                propuesta.getEstado().name(),
                propuesta.getPrecioBase(),
                propuesta.getPorcentajeComision(),
                subasta.getIdSubasta(),
                obtenerTituloSubasta(subasta),
                subasta.getCategoriaMin() != null
                        ? subasta.getCategoriaMin().name()
                        : null,
                subasta.getMoneda() != null
                        ? subasta.getMoneda().name()
                        : null,
                subasta.getFechaInicio(),
                subasta.getUbicacion(),
                obtenerNombreRematador(subasta),
                propuesta.getMotivoRechazoUsuario(),
                propuesta.getFechaCreacion(),
                propuesta.getFechaRespuesta()
        );
    }

    private DevolucionSolicitudDTO toDevolucion(
            DevolucionSolicitud devolucion
    ) {
        return new DevolucionSolicitudDTO(
                devolucion.getIdDevolucion(),
                devolucion.getEstado().name(),
                devolucion.getCosto(),
                devolucion.getMoneda().name(),
                devolucion.getDireccionDestino(),
                devolucion.getMedioPago() != null
                        ? devolucion
                        .getMedioPago()
                        .getIdMedioPago()
                        : null,
                devolucion.getFechaCreacion(),
                devolucion.getFechaPago(),
                devolucion.getFechaEnvio(),
                devolucion.getFechaEntrega()
        );
    }

    private String obtenerTituloSubasta(
            Subasta subasta
    ) {
        if (subasta == null) {
            return null;
        }

        if (subasta.getCatalogo() != null
                && subasta.getCatalogo().getDescripcion() != null) {

            return subasta
                    .getCatalogo()
                    .getDescripcion();
        }

        return "Subasta #" + subasta.getIdSubasta();
    }

    private String obtenerNombreRematador(
            Subasta subasta
    ) {
        if (subasta == null
                || subasta.getRematador() == null) {

            return null;
        }

        String nombre =
                subasta.getRematador().getNombre();

        String apellido =
                subasta.getRematador().getApellido();

        String nombreCompleto =
                ((nombre != null ? nombre : "")
                        + " "
                        + (apellido != null ? apellido : ""))
                        .trim();

        return nombreCompleto.isBlank()
                ? null
                : nombreCompleto;
    }
}