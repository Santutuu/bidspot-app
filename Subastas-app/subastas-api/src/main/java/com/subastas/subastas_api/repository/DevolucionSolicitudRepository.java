package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.DevolucionSolicitud;
import com.subastas.subastas_api.model.EstadoDevolucion;
import com.subastas.subastas_api.model.SolicitudPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DevolucionSolicitudRepository
        extends JpaRepository<DevolucionSolicitud, Long> {

    Optional<DevolucionSolicitud>
    findBySolicitud(
            SolicitudPublicacion solicitud
    );

    Optional<DevolucionSolicitud>
    findBySolicitudAndSolicitudCliente(
            SolicitudPublicacion solicitud,
            Cliente cliente
    );

    List<DevolucionSolicitud>
    findByEstadoOrderByFechaCreacionAsc(
            EstadoDevolucion estado
    );

    boolean existsBySolicitud(
            SolicitudPublicacion solicitud
    );
}