package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Duenio;
import com.subastas.subastas_api.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DuenioRepository
        extends JpaRepository<Duenio, Long> {

    Optional<Duenio> findByPersona(
            Persona persona
    );
}