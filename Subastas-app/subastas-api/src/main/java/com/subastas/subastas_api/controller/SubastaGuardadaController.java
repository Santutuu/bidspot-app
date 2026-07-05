package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.subasta.SubastaHomeDTO;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.service.SubastaGuardadaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/me/subastas-guardadas")
public class SubastaGuardadaController {

    private final SubastaGuardadaService subastaGuardadaService;
    private final UsuarioRepository usuarioRepository;

    public SubastaGuardadaController(SubastaGuardadaService subastaGuardadaService,
                                     UsuarioRepository usuarioRepository) {
        this.subastaGuardadaService = subastaGuardadaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<SubastaHomeDTO>> obtenerGuardadas(Authentication authentication) {
        Usuario usuario = obtenerUsuarioActual(authentication);

        return ResponseEntity.ok(subastaGuardadaService.obtenerGuardadas(usuario));
    }

    @PostMapping("/{idSubasta}")
    public ResponseEntity<Void> guardarSubasta(
            @PathVariable Long idSubasta,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioActual(authentication);

        subastaGuardadaService.guardarSubasta(usuario, idSubasta);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{idSubasta}")
    public ResponseEntity<Void> quitarSubasta(
            @PathVariable Long idSubasta,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioActual(authentication);

        subastaGuardadaService.quitarSubasta(usuario, idSubasta);

        return ResponseEntity.noContent().build();
    }

    private Usuario obtenerUsuarioActual(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        return usuarioRepository.findByMail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no autenticado"
                ));
    }
}