package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Cheque;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChequeRepository extends JpaRepository<Cheque, Long> {
}