package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.subasta.ActivarSubastaRequestDTO;
import com.subastas.subastas_api.DTO.subasta.ActivarSubastaResponseDTO;
import com.subastas.subastas_api.service.AdminSubastaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/subastas")
public class AdminSubastaController {

    private final AdminSubastaService
            adminSubastaService;

    public AdminSubastaController(
            AdminSubastaService adminSubastaService
    ) {
        this.adminSubastaService =
                adminSubastaService;
    }

    /**
     * Activa la subasta y abre un lote.
     *
     * El body es opcional:
     *
     * {
     *   "idItemCatalogo": 7
     * }
     *
     * Si no se envía, abre el primer lote PENDIENTE.
     */
    @PostMapping("/{idSubasta}/activar")
    public ResponseEntity<ActivarSubastaResponseDTO>
    activarSubasta(
            @PathVariable Long idSubasta,
            @RequestBody(required = false)
            ActivarSubastaRequestDTO request
    ) {
        return ResponseEntity.ok(
                adminSubastaService
                        .activarSubasta(
                                idSubasta,
                                request
                        )
        );
    }
}