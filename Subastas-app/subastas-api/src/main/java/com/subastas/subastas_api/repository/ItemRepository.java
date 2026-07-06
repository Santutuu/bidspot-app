package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(
            value = """
                    SELECT i.*
                    FROM item i
                    WHERE i.solicitud_publicacion_id = :idSolicitud
                    ORDER BY i.id_item DESC
                    LIMIT 1
                    """,
            nativeQuery = true
    )
    Optional<Item> findByIdSolicitudPublicacion(@Param("idSolicitud") Long idSolicitud);
}