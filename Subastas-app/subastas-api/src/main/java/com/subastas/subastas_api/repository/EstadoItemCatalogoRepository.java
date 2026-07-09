package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.EstadoItemCatalogo;
import com.subastas.subastas_api.model.EstadoItemCatalogoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoItemCatalogoRepository extends JpaRepository<EstadoItemCatalogoEntity, Long> {

    Optional<EstadoItemCatalogoEntity> findByNombre(EstadoItemCatalogo nombre);
}