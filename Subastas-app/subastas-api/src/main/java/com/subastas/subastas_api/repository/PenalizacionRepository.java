package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.EstadoPenalizacion;
import com.subastas.subastas_api.model.Penalizacion;
import com.subastas.subastas_api.model.TipoPenalizacion;
import com.subastas.subastas_api.model.VentaConcretada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PenalizacionRepository
        extends JpaRepository<Penalizacion, Long> {

    List<Penalizacion>
    findByClienteOrderByFechaGeneracionDesc(
            Cliente cliente
    );

    List<Penalizacion>
    findByClienteAndEstadoOrderByFechaGeneracionDesc(
            Cliente cliente,
            EstadoPenalizacion estado
    );

    List<Penalizacion>
    findByVenta(
            VentaConcretada venta
    );

    boolean existsByClienteAndEstado(
            Cliente cliente,
            EstadoPenalizacion estado
    );

    boolean existsByVentaAndTipo(
            VentaConcretada venta,
            TipoPenalizacion tipo
    );

    Optional<Penalizacion>
    findByVentaAndTipo(
            VentaConcretada venta,
            TipoPenalizacion tipo
    );

    Optional<Penalizacion>
    findByIdPenalizacionAndCliente(
            Long idPenalizacion,
            Cliente cliente
    );
}