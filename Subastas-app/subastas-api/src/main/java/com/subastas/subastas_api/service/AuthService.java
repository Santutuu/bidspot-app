package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.auth.AuthResponseDTO;
import com.subastas.subastas_api.DTO.auth.LoginRequestDTO;
import com.subastas.subastas_api.DTO.auth.RegisterRequestDTO;
import com.subastas.subastas_api.model.EstadoUsuario;
import com.subastas.subastas_api.model.Rol;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDTO register(RegisterRequestDTO request) {
        validarRegistro(request);

        if (usuarioRepository.existsByMail(request.getMail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un usuario registrado con ese mail"
            );
        }

        Usuario usuario = new Usuario(
                request.getNombre(),
                request.getApellido(),
                request.getMail(),
                passwordEncoder.encode(request.getPassword()),
                Rol.USER,
                EstadoUsuario.PENDIENTE_VALIDACION,
                request.getFrenteDNIUrl(),
                request.getDorsoDNIUrl(),
                request.getDomicilio()
        );

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuarioGuardado.getMail());

        return new AuthResponseDTO(
                token,
                usuarioGuardado.getIdUsuario(),
                usuarioGuardado.getNombre(),
                usuarioGuardado.getMail(),
                usuarioGuardado.getRol().name(),
                usuarioGuardado.getEstado().name()
        );
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        validarLogin(request);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getMail(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByMail(request.getMail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Credenciales inválidas"
                ));

        String token = jwtService.generateToken(usuario.getMail());

        return new AuthResponseDTO(
                token,
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getMail(),
                usuario.getRol().name(),
                usuario.getEstado().name()
        );
    }

    private void validarRegistro(RegisterRequestDTO request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }

        if (request.getApellido() == null || request.getApellido().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El apellido es obligatorio");
        }

        if (request.getMail() == null || request.getMail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mail es obligatorio");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos 6 caracteres"
            );
        }

        if (request.getFrenteDNIUrl() == null || request.getFrenteDNIUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La foto del frente del DNI es obligatoria");
        }

        if (request.getDorsoDNIUrl() == null || request.getDorsoDNIUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La foto del dorso del DNI es obligatoria");
        }

        if (request.getDomicilio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El domicilio es obligatorio");
        }
    }

    private void validarLogin(LoginRequestDTO request) {
        if (request.getMail() == null || request.getMail().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mail es obligatorio");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }
    }
}