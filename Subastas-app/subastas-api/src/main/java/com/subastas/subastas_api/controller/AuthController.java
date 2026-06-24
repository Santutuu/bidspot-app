package com.subastas.subastas_api.controller;

import com.subastas.subastas_api.DTO.auth.*;
import com.subastas.subastas_api.model.Usuario;
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

    public AuthController(AuthService authService,
                          UsuarioRepository usuarioRepository) {
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
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

        boolean requiereMedioDePago =
                usuario.getEstado().name().equals("VALIDADO")
                        && usuario.getMediosDePago().isEmpty();

        UsuarioActualDTO response = new UsuarioActualDTO(
                usuario.getIdUsuario(),
                usuario.getPersona().getNombre(),
                usuario.getPersona().getApellido(),
                usuario.getPersona().getMail(),
                usuario.getRol().name(),
                usuario.getEstado().name(),
                categoria,
                usuario.tieneClaveGenerada(),
                requiereMedioDePago
        );

        return ResponseEntity.ok(response);
    }
}