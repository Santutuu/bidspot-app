package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cheque;
import com.subastas.subastas_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChequeRepository extends JpaRepository<Cheque, Long> {

    List<Cheque> findByUsuario(Usuario usuario);
}