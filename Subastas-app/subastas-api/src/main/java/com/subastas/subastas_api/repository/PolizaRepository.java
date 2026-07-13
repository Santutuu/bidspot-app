package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PolizaRepository
        extends JpaRepository<Poliza, Long> {

    Optional<Poliza> findByNroPoliza(
            String nroPoliza
    );

    boolean existsByNroPoliza(
            String nroPoliza
    );
}