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
                    WHERE estado_subasta IN ('CREADA', 'ACTIVA')
                    ORDER BY RANDOM()
                    """,
            nativeQuery = true
    )
    List<Subasta> findRandomActivas(Pageable pageable);

    Subasta findByItemIdItem(Long idItem);


    List<Subasta> findByItemCategoriaAndEstadoSubasta(
            Categoria categoria,
            EstadoSubasta estadoSubasta
    );

}
