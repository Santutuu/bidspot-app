package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.EstadoPropuestaVenta;
import com.subastas.subastas_api.model.PropuestaCondicionesVenta;
import com.subastas.subastas_api.model.SolicitudPublicacion;
import com.subastas.subastas_api.model.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PropuestaCondicionesVentaRepository
        extends JpaRepository<PropuestaCondicionesVenta, Long> {

    Optional<PropuestaCondicionesVenta>
    findBySolicitud(
            SolicitudPublicacion solicitud
    );

    Optional<PropuestaCondicionesVenta>
    findBySolicitudAndEstado(
            SolicitudPublicacion solicitud,
            EstadoPropuestaVenta estado
    );

    List<PropuestaCondicionesVenta>
    findBySubastaAndEstado(
            Subasta subasta,
            EstadoPropuestaVenta estado
    );

    boolean existsBySolicitud(
            SolicitudPublicacion solicitud
    );
}