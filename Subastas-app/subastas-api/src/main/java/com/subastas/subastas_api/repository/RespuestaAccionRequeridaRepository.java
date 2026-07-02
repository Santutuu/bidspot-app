package com.subastas.subastas_api.repository;

import com.subastas.subastas_api.model.AccionRequerida;
import com.subastas.subastas_api.model.RespuestaAccionRequerida;
import com.subastas.subastas_api.model.SolicitudPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespuestaAccionRequeridaRepository extends JpaRepository<RespuestaAccionRequerida, Long> {

    List<RespuestaAccionRequerida> findBySolicitudPublicacion(SolicitudPublicacion solicitudPublicacion);

    boolean existsBySolicitudPublicacionAndAccion(
            SolicitudPublicacion solicitudPublicacion,
            AccionRequerida accion
    );
}