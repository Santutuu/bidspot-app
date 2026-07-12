package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.Item;
import com.subastas.subastas_api.model.RegistroSubasta;
import com.subastas.subastas_api.model.Subasta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistroSubastaRepository
        extends JpaRepository<RegistroSubasta, Long> {

    Optional<RegistroSubasta>
    findBySubastaAndProducto(
            Subasta subasta,
            Item producto
    );
}