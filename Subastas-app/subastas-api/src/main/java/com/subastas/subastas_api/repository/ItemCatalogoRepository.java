package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Catalogo;
import com.subastas.subastas_api.model.EstadoItemCatalogo;
import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.Subasta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemCatalogoRepository
        extends JpaRepository<ItemCatalogo, Long> {

    /*
     * Obtiene el lote actualmente abierto de una subasta.
     */
    @Query("""
            SELECT ic
            FROM ItemCatalogo ic
            JOIN ic.catalogo c
            WHERE c.subasta = :subasta
              AND ic.estado.nombre = :estado
            """)
    Optional<ItemCatalogo>
    findItemActualBySubastaAndEstado(
            @Param("subasta")
            Subasta subasta,

            @Param("estado")
            EstadoItemCatalogo estado
    );

    /*
     * Igual que la consulta anterior, pero bloquea el lote
     * mientras se registra una nueva puja.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT ic
            FROM ItemCatalogo ic
            JOIN ic.catalogo c
            WHERE c.subasta = :subasta
              AND ic.estado.nombre = :estado
            """)
    Optional<ItemCatalogo>
    findItemActualBySubastaAndEstadoForUpdate(
            @Param("subasta")
            Subasta subasta,

            @Param("estado")
            EstadoItemCatalogo estado
    );

    /*
     * Busca el primer lote de un catálogo que tenga
     * el estado normalizado solicitado.
     *
     * Se usa tanto para:
     *
     * - activar una subasta;
     * - abrir el siguiente lote después de cerrar uno.
     *
     * Se utiliza SQL nativo porque la estructura física es legacy:
     *
     * itemscatalogo.estadoitemcatalogo
     *              ↓
     * estadositemcatalogo.identificador
     */
    @Query(
            value = """
                    SELECT ic.*
                    FROM itemscatalogo ic
                    INNER JOIN estadositemcatalogo e
                        ON e.identificador = ic.estadoitemcatalogo
                    WHERE ic.catalogo = :idCatalogo
                      AND UPPER(e.nombre) = UPPER(:estado)
                    ORDER BY ic.identificador ASC
                    LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<ItemCatalogo>
    findFirstByCatalogoIdAndEstado(
            @Param("idCatalogo")
            Long idCatalogo,

            @Param("estado")
            String estado
    );

    /*
     * Devuelve todos los lotes de un catálogo
     * que tengan un determinado estado.
     */
    @Query("""
            SELECT ic
            FROM ItemCatalogo ic
            WHERE ic.catalogo = :catalogo
              AND ic.estado.nombre = :estado
            ORDER BY ic.idItemCatalogo ASC
            """)
    List<ItemCatalogo>
    findByCatalogoAndEstado(
            @Param("catalogo")
            Catalogo catalogo,

            @Param("estado")
            EstadoItemCatalogo estado
    );
}