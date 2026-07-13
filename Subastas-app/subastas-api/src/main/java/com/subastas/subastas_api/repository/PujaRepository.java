package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.Puja;
import com.subastas.subastas_api.model.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PujaRepository
        extends JpaRepository<Puja, Long> {

    @Query("""
            SELECT p
            FROM Puja p
            WHERE p.asistente.subasta = :subasta
            ORDER BY p.fechaHora ASC
            """)
    List<Puja> findBySubastaOrderByFechaHoraAsc(
            Subasta subasta
    );

    @Query("""
            SELECT p
            FROM Puja p
            WHERE p.itemCatalogo = :itemCatalogo
              AND p.asistente.cliente = :cliente
            ORDER BY p.monto DESC
            LIMIT 1
            """)
    Optional<Puja>
    findTopByItemCatalogoAndClienteOrderByMontoDesc(
            ItemCatalogo itemCatalogo,
            Cliente cliente
    );

    void deleteByItemCatalogo(
            ItemCatalogo itemCatalogo
    );

    @Query("""
            SELECT p
            FROM Puja p
            WHERE p.itemCatalogo = :itemCatalogo
            ORDER BY p.monto DESC
            LIMIT 1
            """)
    Optional<Puja>
    findTopByItemCatalogoOrderByMontoDesc(
            ItemCatalogo itemCatalogo
    );
}