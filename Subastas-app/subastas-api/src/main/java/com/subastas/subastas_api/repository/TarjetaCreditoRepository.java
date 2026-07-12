package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.TarjetaCredito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarjetaCreditoRepository
        extends JpaRepository<TarjetaCredito, Long> {

    List<TarjetaCredito> findByCliente(Cliente cliente);

    long countByCliente(Cliente cliente);

    Optional<TarjetaCredito> findByIdMedioPagoAndCliente(
            Long idMedioPago,
            Cliente cliente
    );
}