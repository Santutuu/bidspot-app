package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Catalogo;
import com.subastas.subastas_api.model.EstadoItemCatalogo;
import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.Subasta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ItemCatalogoRepository extends JpaRepository<ItemCatalogo, Long> {

    @Query("""
            SELECT ic
            FROM ItemCatalogo ic
            JOIN ic.catalogo c
            WHERE c.subasta = :subasta
              AND ic.estado.nombre = :estado
            """)
    Optional<ItemCatalogo> findItemActualBySubastaAndEstado(
            Subasta subasta,
            EstadoItemCatalogo estado
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT ic
            FROM ItemCatalogo ic
            JOIN ic.catalogo c
            WHERE c.subasta = :subasta
              AND ic.estado.nombre = :estado
            """)
    Optional<ItemCatalogo> findItemActualBySubastaAndEstadoForUpdate(
            Subasta subasta,
            EstadoItemCatalogo estado
    );

    @Query("""
            SELECT ic
            FROM ItemCatalogo ic
            WHERE ic.catalogo = :catalogo
              AND ic.estado.nombre = :estado
            ORDER BY ic.idItemCatalogo ASC
            LIMIT 1
            """)
    Optional<ItemCatalogo> findFirstByCatalogoAndEstadoOrderByIdItemCatalogoAsc(
            Catalogo catalogo,
            EstadoItemCatalogo estado
    );

    @Query("""
            SELECT ic
            FROM ItemCatalogo ic
            WHERE ic.catalogo = :catalogo
              AND ic.estado.nombre = :estado
            ORDER BY ic.idItemCatalogo ASC
            """)
    List<ItemCatalogo> findByCatalogoAndEstado(
            Catalogo catalogo,
            EstadoItemCatalogo estado
    );
}