package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.SolicitudPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitudPublicacionRepository
        extends JpaRepository<SolicitudPublicacion, Long> {

    List<SolicitudPublicacion>
    findByClienteOrderByFechaCreacionDesc(
            Cliente cliente
    );

    Optional<SolicitudPublicacion>
    findByIdSolicitudAndCliente(
            Long idSolicitud,
            Cliente cliente
    );
}