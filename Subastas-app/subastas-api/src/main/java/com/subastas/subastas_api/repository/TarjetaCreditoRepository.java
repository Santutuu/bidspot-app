package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.TarjetaCredito;
import com.subastas.subastas_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarjetaCreditoRepository extends JpaRepository<TarjetaCredito, Long> {

    List<TarjetaCredito> findByUsuario(Usuario usuario);

    long countByUsuario(Usuario usuario);

    Optional<TarjetaCredito> findByIdMedioPagoAndUsuario(Long idMedioPago, Usuario usuario);
}