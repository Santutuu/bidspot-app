package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.EstadoItemCatalogo;
import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ItemCatalogoRepository extends JpaRepository<ItemCatalogo, Long> {

    @Query("""
            SELECT ic
            FROM ItemCatalogo ic
            JOIN ic.catalogo c
            WHERE c.subasta = :subasta
              AND ic.estado = :estado
            """)
    Optional<ItemCatalogo> findItemActualBySubastaAndEstado(
            Subasta subasta,
            EstadoItemCatalogo estado
    );
}