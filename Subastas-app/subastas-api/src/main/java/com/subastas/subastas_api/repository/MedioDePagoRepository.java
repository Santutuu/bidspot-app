package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cheque;
import com.subastas.subastas_api.model.MedioDePago;
import com.subastas.subastas_api.model.TarjetaCredito;
import com.subastas.subastas_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedioDePagoRepository extends JpaRepository<MedioDePago, Long> {

    List<MedioDePago> findByUsuario(Usuario usuario);

    List<TarjetaCredito> findTarjetaByUsuarioAndClass(Usuario usuario, Class<TarjetaCredito> tipo);

    List<Cheque> findChequeByUsuarioAndClass(Usuario usuario, Class<Cheque> tipo);

    Optional<MedioDePago> findByIdMedioPagoAndUsuario(Long idMedioPago, Usuario usuario);
}