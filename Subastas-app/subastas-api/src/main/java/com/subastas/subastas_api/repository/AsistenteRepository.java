package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Asistente;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AsistenteRepository extends JpaRepository<Asistente, Integer> {

    Optional<Asistente> findByClienteAndSubasta(Cliente cliente, Subasta subasta);
}