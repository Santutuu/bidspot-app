package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.SolicitudPublicacion;
import com.subastas.subastas_api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SolicitudPublicacionRepository extends JpaRepository<SolicitudPublicacion, Long> {

    List<SolicitudPublicacion> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);

    Optional<SolicitudPublicacion> findByIdSolicitudAndUsuario(Long idSolicitud, Usuario usuario);
}