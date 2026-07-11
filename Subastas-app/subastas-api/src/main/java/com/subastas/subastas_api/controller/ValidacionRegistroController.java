package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.admin.AprobacionUsuarioResponseDTO;
import com.subastas.subastas_api.DTO.admin.AprobarUsuarioRequestDTO;
import com.subastas.subastas_api.service.ValidacionRegistroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/usuarios")
public class ValidacionRegistroController {

    private final ValidacionRegistroService validacionRegistroService;

    public ValidacionRegistroController(
            ValidacionRegistroService validacionRegistroService
    ) {
        this.validacionRegistroService = validacionRegistroService;
    }

    @PostMapping("/{idUsuario}/aprobar")
    public ResponseEntity<AprobacionUsuarioResponseDTO> aprobar(
            @PathVariable Long idUsuario,
            @RequestBody AprobarUsuarioRequestDTO request
    ) {
        return ResponseEntity.ok(
                validacionRegistroService.aprobarUsuario(
                        idUsuario,
                        request
                )
        );
    }

    @PostMapping("/{idUsuario}/rechazar")
    public ResponseEntity<AprobacionUsuarioResponseDTO> rechazar(
            @PathVariable Long idUsuario
    ) {
        return ResponseEntity.ok(
                validacionRegistroService.rechazarUsuario(idUsuario)
        );
    }
}