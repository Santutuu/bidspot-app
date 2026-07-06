package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.poliza.AumentarPolizaRequestDTO;
import com.subastas.subastas_api.DTO.poliza.PolizaSolicitudResponseDTO;
import com.subastas.subastas_api.service.PolizaSolicitudService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me/solicitudes-publicacion")
public class PolizaSolicitudController {

    private final PolizaSolicitudService polizaSolicitudService;

    public PolizaSolicitudController(
            PolizaSolicitudService polizaSolicitudService) {

        this.polizaSolicitudService = polizaSolicitudService;
    }

    @GetMapping("/{idSolicitud}/poliza")
    public ResponseEntity<PolizaSolicitudResponseDTO> obtenerPoliza(
            @PathVariable Long idSolicitud,
            Authentication authentication) {

        return ResponseEntity.ok(
                polizaSolicitudService.obtenerPoliza(
                        idSolicitud,
                        authentication
                )
        );
    }

    @PostMapping("/{idSolicitud}/poliza/aumentar")
    public ResponseEntity<PolizaSolicitudResponseDTO> aumentarPoliza(
            @PathVariable Long idSolicitud,
            @RequestBody AumentarPolizaRequestDTO request,
            Authentication authentication) {

        return ResponseEntity.ok(
                polizaSolicitudService.aumentarPoliza(
                        idSolicitud,
                        request,
                        authentication
                )
        );
    }
}