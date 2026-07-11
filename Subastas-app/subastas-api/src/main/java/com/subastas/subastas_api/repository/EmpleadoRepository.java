package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
}