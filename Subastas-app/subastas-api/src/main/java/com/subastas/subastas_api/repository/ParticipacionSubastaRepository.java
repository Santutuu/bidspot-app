package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.EstadoParticipacionSubasta;
import com.subastas.subastas_api.model.ParticipacionSubasta;
import com.subastas.subastas_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipacionSubastaRepository extends JpaRepository<ParticipacionSubasta, Long> {

    Optional<ParticipacionSubasta> findByUsuarioAndEstado(
            Usuario usuario,
            EstadoParticipacionSubasta estado
    );
}