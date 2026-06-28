package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Subasta;

import com.subastas.subastas_api.model.Categoria;
import com.subastas.subastas_api.model.EstadoSubasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubastaRepository extends JpaRepository<Subasta, Long> {

    @Query(
            value = """
                    SELECT *
                    FROM subasta
                    WHERE estado_subasta IN ('PROGRAMADA', 'ACTIVA')
                    ORDER BY RANDOM()
                    """,
            nativeQuery = true
    )
    List<Subasta> findRandomActivas(Pageable pageable);

    @Query("""
            SELECT s
            FROM Subasta s
            JOIN s.catalogo c
            JOIN c.items ic
            WHERE ic.item.idItem = :idItem
            """)
    Subasta findByItemIdItem(Long idItem);


    @Query("""
            SELECT DISTINCT s
            FROM Subasta s
            JOIN s.catalogo c
            JOIN c.items ic
            WHERE ic.item.categoria = :categoria
              AND s.estadoSubasta = :estadoSubasta
            """)
    List<Subasta> findByItemCategoriaAndEstadoSubasta(
            Categoria categoria,
            EstadoSubasta estadoSubasta
    );

}
