package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.EstadoVenta;
import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.VentaConcretada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VentaConcretadaRepository
        extends JpaRepository<VentaConcretada, Long> {

    Optional<VentaConcretada>
    findByItemCatalogo(
            ItemCatalogo itemCatalogo
    );

    void deleteByItemCatalogo(
            ItemCatalogo itemCatalogo
    );

    List<VentaConcretada>
    findByCompradorOrderByFechaVentaDesc(
            Cliente comprador
    );

    Optional<VentaConcretada>
    findByIdVentaAndComprador(
            Long idVenta,
            Cliente comprador
    );

    List<VentaConcretada>
    findByEstadoAndFechaLimitePagoLessThanEqual(
            EstadoVenta estado,
            LocalDateTime fechaLimite
    );

    Optional<VentaConcretada>
    findTopByCompradorOrderByFechaVentaDesc(
            Cliente comprador
    );
}