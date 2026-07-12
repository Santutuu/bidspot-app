package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.VentaConcretada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VentaConcretadaRepository
        extends JpaRepository<VentaConcretada, Long> {

    List<VentaConcretada> findByCompradorOrderByFechaVentaDesc(
            Cliente comprador
    );

    Optional<VentaConcretada> findByIdVentaAndComprador(
            Long idVenta,
            Cliente comprador
    );

    Optional<VentaConcretada> findByItemCatalogo(
            ItemCatalogo itemCatalogo
    );

    void deleteByItemCatalogo(
            ItemCatalogo itemCatalogo
    );
}