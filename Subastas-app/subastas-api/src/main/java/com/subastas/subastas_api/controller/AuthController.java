package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.auth.AuthResponseDTO;
import com.subastas.subastas_api.DTO.auth.CompleteRegistrationRequestDTO;
import com.subastas.subastas_api.DTO.auth.LoginRequestDTO;
import com.subastas.subastas_api.DTO.auth.PreRegisterRequestDTO;
import com.subastas.subastas_api.DTO.auth.PreRegisterResponseDTO;
import com.subastas.subastas_api.DTO.auth.RegistrationStatusDTO;
import com.subastas.subastas_api.DTO.auth.UsuarioActualDTO;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.TarjetaCreditoRepository;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final TarjetaCreditoRepository tarjetaCreditoRepository;

    public AuthController(AuthService authService,
                          UsuarioRepository usuarioRepository,
                          TarjetaCreditoRepository tarjetaCreditoRepository) {
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
        this.tarjetaCreditoRepository = tarjetaCreditoRepository;
    }

    @PostMapping("/pre-register")
    public ResponseEntity<PreRegisterResponseDTO> preRegister(
            @RequestBody PreRegisterRequestDTO request
    ) {
        PreRegisterResponseDTO response = authService.preRegister(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/registration-status")
    public ResponseEntity<RegistrationStatusDTO> registrationStatus(
            @RequestParam String mail
    ) {
        RegistrationStatusDTO response = authService.obtenerEstadoRegistro(mail);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete-registration")
    public ResponseEntity<AuthResponseDTO> completeRegistration(
            @RequestBody CompleteRegistrationRequestDTO request
    ) {
        AuthResponseDTO response = authService.completarRegistro(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody LoginRequestDTO request
    ) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioActualDTO> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        String mail = authentication.getName();

        Usuario usuario = usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no autenticado"
                ));

        String categoria = usuario.getCategoria() != null
                ? usuario.getCategoria().name()
                : null;

        boolean tieneCuentaBanco = usuario.getCuenta() != null;

        boolean tieneMedioPago = tarjetaCreditoRepository.countByUsuario(usuario) > 0;

        boolean configuracionFinancieraCompleta =
                tieneCuentaBanco && tieneMedioPago;

        UsuarioActualDTO response = new UsuarioActualDTO(
                usuario.getIdUsuario(),
                usuario.getPersona().getNombre(),
                usuario.getPersona().getApellido(),
                usuario.getPersona().getMail(),
                usuario.getRol().name(),
                usuario.getEstado().name(),
                categoria,
                usuario.tieneClaveGenerada(),
                tieneCuentaBanco,
                tieneMedioPago,
                configuracionFinancieraCompleta
        );

        return ResponseEntity.ok(response);
    }
}
