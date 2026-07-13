package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.AccionSolicitudPublicacion;
import com.subastas.subastas_api.model.EstadoAccionSolicitud;
import com.subastas.subastas_api.model.SolicitudPublicacion;
import com.subastas.subastas_api.model.TipoAccionSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccionSolicitudPublicacionRepository
        extends JpaRepository<AccionSolicitudPublicacion, Long> {

    List<AccionSolicitudPublicacion>
    findBySolicitudOrderByFechaCreacionAsc(
            SolicitudPublicacion solicitud
    );

    List<AccionSolicitudPublicacion>
    findBySolicitudAndEstadoOrderByFechaCreacionAsc(
            SolicitudPublicacion solicitud,
            EstadoAccionSolicitud estado
    );

    Optional<AccionSolicitudPublicacion>
    findByIdAccionAndSolicitud(
            Long idAccion,
            SolicitudPublicacion solicitud
    );

    Optional<AccionSolicitudPublicacion>
    findFirstBySolicitudAndTipoAndEstadoOrderByFechaCreacionDesc(
            SolicitudPublicacion solicitud,
            TipoAccionSolicitud tipo,
            EstadoAccionSolicitud estado
    );

    boolean existsBySolicitudAndTipoAndEstado(
            SolicitudPublicacion solicitud,
            TipoAccionSolicitud tipo,
            EstadoAccionSolicitud estado
    );
}