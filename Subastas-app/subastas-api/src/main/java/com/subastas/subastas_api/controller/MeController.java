package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.cuenta.CuentaCobroRequestDTO;
import com.subastas.subastas_api.DTO.cuenta.CuentaCobroResponseDTO;
import com.subastas.subastas_api.DTO.mediosPago.*;
import com.subastas.subastas_api.service.MeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/me")
public class MeController {

    private final MeService meService;

    public MeController(MeService meService) {
        this.meService = meService;
    }

    @GetMapping("/cuenta-cobro")
    public ResponseEntity<CuentaCobroResponseDTO> obtenerCuentaCobro(
            Authentication authentication
    ) {
        CuentaCobroResponseDTO response = meService.obtenerCuentaCobro(
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/cuenta-cobro")
    public ResponseEntity<CuentaCobroResponseDTO> crearCuentaCobro(
            Authentication authentication,
            @RequestBody CuentaCobroRequestDTO request
    ) {
        CuentaCobroResponseDTO response = meService.crearCuentaCobro(
                authentication.getName(),
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/medios-pago")
    public ResponseEntity<List<MedioPagoResponseDTO>> obtenerMediosPago(
            Authentication authentication
    ) {
        List<MedioPagoResponseDTO> response = meService.obtenerMediosPago(
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/medios-pago/tarjetas")
    public ResponseEntity<List<TarjetaResponseDTO>> obtenerTarjetas(
            Authentication authentication
    ) {
        List<TarjetaResponseDTO> response = meService.obtenerTarjetas(
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/medios-pago/tarjetas")
    public ResponseEntity<TarjetaResponseDTO> crearTarjeta(
            Authentication authentication,
            @RequestBody TarjetaRequestDTO request
    ) {
        TarjetaResponseDTO response = meService.crearTarjeta(
                authentication.getName(),
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/medios-pago/cheques")
    public ResponseEntity<List<ChequeResponseDTO>> obtenerCheques(
            Authentication authentication
    ) {
        List<ChequeResponseDTO> response = meService.obtenerCheques(
                authentication.getName()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/medios-pago/cheques")
    public ResponseEntity<ChequeResponseDTO> crearCheque(
            Authentication authentication,
            @RequestBody ChequeRequestDTO request
    ) {
        ChequeResponseDTO response = meService.crearCheque(
                authentication.getName(),
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}