package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.publicacion.*;
import com.subastas.subastas_api.model.AccionRequerida;
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

    public SolicitudPublicacionController(SolicitudPublicacionService solicitudService,
                                          UsuarioRepository usuarioRepository) {
        this.solicitudService = solicitudService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<SolicitudPublicacionDetalleDTO> crearSolicitud(
            @RequestBody SolicitudPublicacionRequestDTO request,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioActual(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(solicitudService.crearSolicitud(usuario, request));
    }

    @GetMapping
    public ResponseEntity<List<SolicitudPublicacionResumenDTO>> listarMisSolicitudes(
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioActual(authentication);
        return ResponseEntity.ok(solicitudService.listarMisSolicitudes(usuario));
    }

    @GetMapping("/{idSolicitud}")
    public ResponseEntity<SolicitudPublicacionDetalleDTO> obtenerDetalle(
            @PathVariable Long idSolicitud,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioActual(authentication);
        return ResponseEntity.ok(solicitudService.obtenerDetalle(idSolicitud, usuario));
    }

    @PostMapping("/{idSolicitud}/acciones/{accion}/resolver")
    public ResponseEntity<SolicitudPublicacionDetalleDTO> responderAccion(
            @PathVariable Long idSolicitud,
            @PathVariable AccionRequerida accion,
            @RequestBody ResponderAccionRequestDTO request,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioActual(authentication);
        return ResponseEntity.ok(
                solicitudService.responderAccion(idSolicitud, accion, usuario, request)
        );
    }

    @DeleteMapping("/{idSolicitud}")
    public ResponseEntity<Void> cancelarSolicitud(
            @PathVariable Long idSolicitud,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioActual(authentication);
        solicitudService.cancelarSolicitud(idSolicitud, usuario);
        return ResponseEntity.noContent().build();
    }

    private Usuario obtenerUsuarioActual(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        return usuarioRepository.findByMail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no autenticado"
                ));
    }
}