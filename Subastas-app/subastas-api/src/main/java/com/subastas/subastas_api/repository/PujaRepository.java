package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.Puja;
import com.subastas.subastas_api.model.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
            @Param("subasta")
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
            @Param("itemCatalogo")
            ItemCatalogo itemCatalogo,

            @Param("cliente")
            Cliente cliente
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
            @Param("itemCatalogo")
            ItemCatalogo itemCatalogo
    );

    /**
     * Elimina todas las pujas correspondientes a un lote.
     *
     * Se utiliza cuando se reabre el lote para que vuelva
     * a comenzar desde el precio base y sin mejor postor.
     */
    @Modifying(flushAutomatically = true)
    @Query("""
            DELETE FROM Puja p
            WHERE p.itemCatalogo = :itemCatalogo
            """)
    int deleteTodasPorItemCatalogo(
            @Param("itemCatalogo")
            ItemCatalogo itemCatalogo
    );
}