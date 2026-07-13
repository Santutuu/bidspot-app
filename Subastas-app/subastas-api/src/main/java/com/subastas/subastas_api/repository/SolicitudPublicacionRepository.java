package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Categoria;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.EstadoSolicitud;
import com.subastas.subastas_api.model.SolicitudPublicacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<SolicitudPublicacion>
    findByEstadoOrderByFechaCreacionAsc(
            EstadoSolicitud estado,
            Pageable pageable
    );

    Page<SolicitudPublicacion>
    findByCategoriaOrderByFechaCreacionAsc(
            Categoria categoria,
            Pageable pageable
    );

    Page<SolicitudPublicacion>
    findByEstadoAndCategoriaOrderByFechaCreacionAsc(
            EstadoSolicitud estado,
            Categoria categoria,
            Pageable pageable
    );

    List<SolicitudPublicacion>
    findByEstadoAndItemIsNullOrderByFechaCreacionAsc(
            EstadoSolicitud estado
    );

    boolean existsByIdSolicitudAndCliente(
            Long idSolicitud,
            Cliente cliente
    );
}