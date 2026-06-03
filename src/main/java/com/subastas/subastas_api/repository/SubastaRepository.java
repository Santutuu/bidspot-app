package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SubastaRepository extends JpaRepository<Subasta, Long> {

    @Query(
            value = """
                    SELECT *
                    FROM subasta
                    WHERE estado_subasta IN ('ABIERTA', 'PROGRAMADA')
                    ORDER BY RANDOM()
                    """,
            nativeQuery = true
    )
    List<Subasta> findRandomActivas(Pageable pageable);

    Subasta findByItemIdItem(Long idItem);}
