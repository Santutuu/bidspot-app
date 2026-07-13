package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.poliza.CrearPolizaSolicitudRequestDTO;
import com.subastas.subastas_api.DTO.poliza.PolizaSolicitudResponseDTO;
import com.subastas.subastas_api.service.AdminPolizaSolicitudService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/solicitudes-publicacion")
public class AdminPolizaSolicitudController {

    private final AdminPolizaSolicitudService
            polizaService;

    public AdminPolizaSolicitudController(
            AdminPolizaSolicitudService polizaService
    ) {
        this.polizaService = polizaService;
    }

    /*
     * La empresa crea y contrata la póliza del producto.
     */
    @PostMapping("/{idSolicitud}/poliza")
    public ResponseEntity<PolizaSolicitudResponseDTO>
    crearPoliza(
            @PathVariable Long idSolicitud,
            @RequestBody
            CrearPolizaSolicitudRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        polizaService.crearPoliza(
                                idSolicitud,
                                request
                        )
                );
    }

    /*
     * La empresa consulta la póliza de cualquier solicitud.
     */
    @GetMapping("/{idSolicitud}/poliza")
    public ResponseEntity<PolizaSolicitudResponseDTO>
    obtenerPoliza(
            @PathVariable Long idSolicitud
    ) {
        return ResponseEntity.ok(
                polizaService.obtenerPoliza(
                        idSolicitud
                )
        );
    }
}