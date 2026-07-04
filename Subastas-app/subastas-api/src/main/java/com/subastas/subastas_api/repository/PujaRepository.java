package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Puja;
import com.subastas.subastas_api.model.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PujaRepository extends JpaRepository<Puja, Long> {

    List<Puja> findBySubastaOrderByFechaHoraAsc(Subasta subasta);
}