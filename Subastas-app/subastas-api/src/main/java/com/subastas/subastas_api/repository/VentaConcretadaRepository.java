package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.model.VentaConcretada;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VentaConcretadaRepository extends JpaRepository<VentaConcretada, Long> {

    List<VentaConcretada> findByCompradorOrderByFechaVentaDesc(Usuario comprador);

    Optional<VentaConcretada> findByIdVentaAndComprador(Long idVenta, Usuario comprador);

    Optional<VentaConcretada> findByItemCatalogo(ItemCatalogo itemCatalogo);

    void deleteByItemCatalogo(ItemCatalogo itemCatalogo);
}