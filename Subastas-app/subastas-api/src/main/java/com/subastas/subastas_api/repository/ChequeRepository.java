package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cheque;
import com.subastas.subastas_api.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChequeRepository
        extends JpaRepository<Cheque, Long> {

    List<Cheque> findByCliente(Cliente cliente);

    long countByCliente(Cliente cliente);

    Optional<Cheque> findByIdMedioPagoAndCliente(
            Long idMedioPago,
            Cliente cliente
    );
}