package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.publicacion.ConfigurarDevolucionRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.ResolverAccionSolicitudRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionDetalleDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionRequestDTO;
import com.subastas.subastas_api.DTO.publicacion.SolicitudPublicacionResumenDTO;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.service.SolicitudPublicacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/me/solicitudes-publicacion")
public class SolicitudPublicacionController {

    private final SolicitudPublicacionService solicitudService;
    private final UsuarioRepository usuarioRepository;

    public SolicitudPublicacionController(
            SolicitudPublicacionService solicitudService,
            UsuarioRepository usuarioRepository
    ) {
        this.solicitudService = solicitudService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    crearSolicitud(
            @RequestBody SolicitudPublicacionRequestDTO request,
            Authentication authentication
    ) {
        Usuario usuario =
                obtenerUsuarioActual(authentication);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        solicitudService.crearSolicitud(
                                usuario,
                                request
                        )
                );
    }

    @GetMapping
    public ResponseEntity<List<SolicitudPublicacionResumenDTO>>
    listarMisSolicitudes(
            Authentication authentication
    ) {
        Usuario usuario =
                obtenerUsuarioActual(authentication);

        return ResponseEntity.ok(
                solicitudService.listarMisSolicitudes(usuario)
        );
    }

    @GetMapping("/{idSolicitud}")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    obtenerDetalle(
            @PathVariable Long idSolicitud,
            Authentication authentication
    ) {
        Usuario usuario =
                obtenerUsuarioActual(authentication);

        return ResponseEntity.ok(
                solicitudService.obtenerDetalle(
                        idSolicitud,
                        usuario
                )
        );
    }

    @PostMapping("/{idSolicitud}/acciones/{idAccion}/resolver")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    resolverAccion(
            @PathVariable Long idSolicitud,
            @PathVariable Long idAccion,
            @RequestBody ResolverAccionSolicitudRequestDTO request,
            Authentication authentication
    ) {
        Usuario usuario =
                obtenerUsuarioActual(authentication);

        return ResponseEntity.ok(
                solicitudService.resolverAccion(
                        idSolicitud,
                        idAccion,
                        usuario,
                        request
                )
        );
    }

    @PutMapping("/{idSolicitud}/devolucion")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    configurarDevolucion(
            @PathVariable Long idSolicitud,
            @RequestBody ConfigurarDevolucionRequestDTO request,
            Authentication authentication
    ) {
        Usuario usuario =
                obtenerUsuarioActual(authentication);

        return ResponseEntity.ok(
                solicitudService.configurarDevolucion(
                        idSolicitud,
                        usuario,
                        request
                )
        );
    }

    @PostMapping("/{idSolicitud}/devolucion/confirmar-pago")
    public ResponseEntity<SolicitudPublicacionDetalleDTO>
    confirmarPagoDevolucion(
            @PathVariable Long idSolicitud,
            Authentication authentication
    ) {
        Usuario usuario =
                obtenerUsuarioActual(authentication);

        return ResponseEntity.ok(
                solicitudService.confirmarPagoDevolucion(
                        idSolicitud,
                        usuario
                )
        );
    }

    @DeleteMapping("/{idSolicitud}")
    public ResponseEntity<Void>
    cancelarSolicitud(
            @PathVariable Long idSolicitud,
            Authentication authentication
    ) {
        Usuario usuario =
                obtenerUsuarioActual(authentication);

        solicitudService.cancelarSolicitud(
                idSolicitud,
                usuario
        );

        return ResponseEntity.noContent().build();
    }

    private Usuario obtenerUsuarioActual(
            Authentication authentication
    ) {
        if (authentication == null
                || authentication.getName() == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        return usuarioRepository
                .findByMail(
                        authentication.getName()
                                .trim()
                                .toLowerCase()
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Usuario no autenticado"
                        )
                );
    }
}