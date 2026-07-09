package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}