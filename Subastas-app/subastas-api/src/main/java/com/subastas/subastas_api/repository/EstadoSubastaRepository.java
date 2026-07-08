package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.EstadoSubasta;
import com.subastas.subastas_api.model.EstadoSubastaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstadoSubastaRepository
        extends JpaRepository<EstadoSubastaEntity, Long> {

    Optional<EstadoSubastaEntity> findByNombre(EstadoSubasta nombre);
}