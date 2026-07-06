package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.Puja;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PujaRepository extends JpaRepository<Puja, Long> {

    List<Puja> findBySubastaOrderByFechaHoraAsc(Subasta subasta);

    Optional<Puja> findTopByItemCatalogoAndUsuarioOrderByMontoDesc(
            ItemCatalogo itemCatalogo,
            Usuario usuario
    );
}