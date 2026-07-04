package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.puja.EstadoPujaSubastaResponseDTO;
import com.subastas.subastas_api.DTO.puja.PujaRequestDTO;
import com.subastas.subastas_api.DTO.puja.PujaResponseDTO;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.service.PujaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/subastas/{idSubasta}")
public class PujaController {

    private final PujaService pujaService;
    private final UsuarioRepository usuarioRepository;

    public PujaController(PujaService pujaService,
                          UsuarioRepository usuarioRepository) {
        this.pujaService = pujaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/puja/estado")
    public ResponseEntity<EstadoPujaSubastaResponseDTO> obtenerEstadoPuja(
            @PathVariable Long idSubasta
    ) {
        EstadoPujaSubastaResponseDTO response =
                pujaService.obtenerEstadoPuja(idSubasta);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/pujas")
    public ResponseEntity<PujaResponseDTO> realizarPuja(
            @PathVariable Long idSubasta,
            @RequestBody PujaRequestDTO request,
            Authentication authentication
    ) {
        Usuario usuario = obtenerUsuarioActual(authentication);

        PujaResponseDTO response =
                pujaService.realizarPuja(idSubasta, usuario, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    private Usuario obtenerUsuarioActual(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesión para pujar"
            );
        }

        return usuarioRepository.findByMail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no autenticado"
                ));
    }
}