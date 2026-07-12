package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.MedioDePago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedioDePagoRepository
        extends JpaRepository<MedioDePago, Long> {

    List<MedioDePago> findByCliente(Cliente cliente);

    Optional<MedioDePago> findByIdMedioPagoAndCliente(
            Long idMedioPago,
            Cliente cliente
    );
}