package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.auth.*;
import com.subastas.subastas_api.exception.EmailAlreadyExistsException;
import com.subastas.subastas_api.exception.InvalidCredentialsException;
import com.subastas.subastas_api.exception.UserBlockedException;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.TarjetaCreditoRepository;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final EmailService emailService;
    private final TarjetaCreditoRepository tarjetaCreditoRepository;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager,
                       EmailService emailService,
                       TarjetaCreditoRepository tarjetaCreditoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.tarjetaCreditoRepository = tarjetaCreditoRepository;
    }

    public PreRegisterResponseDTO preRegister(PreRegisterRequestDTO request) {
        String mailNormalizado = request.getMail().trim().toLowerCase();

        if (usuarioRepository.existsByMail(mailNormalizado)) {
            throw new EmailAlreadyExistsException(mailNormalizado);
        }

        validarPreRegister(request);

        Persona persona = new Persona(
                request.getNombre().trim(),
                request.getApellido().trim(),
                mailNormalizado,
                request.getFrenteDNIUrl(),
                request.getDorsoDNIUrl(),
                request.getDomicilio()
        );

        Usuario usuario = new Usuario(
                persona,
                null,
                Rol.USER,
                EstadoUsuario.PENDIENTE_VALIDACION
        );

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        emailService.enviarSolicitudRecibida(
                usuarioGuardado.getPersona().getMail(),
                usuarioGuardado.getPersona().getNombre()
        );

        return new PreRegisterResponseDTO(
                usuarioGuardado.getIdUsuario(),
                usuarioGuardado.getPersona().getNombre(),
                usuarioGuardado.getPersona().getMail(),
                usuarioGuardado.getEstado().name(),
                "Tu solicitud fue enviada y se encuentra pendiente de validación."
        );
    }

    public RegistrationStatusDTO obtenerEstadoRegistro(String mail) {
        String mailNormalizado = mail.trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByMail(mailNormalizado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una solicitud con ese email"
                ));

        String categoria = usuario.getCategoria() != null
                ? usuario.getCategoria().name()
                : null;

        boolean puedeGenerarClave =
                usuario.getEstado() == EstadoUsuario.VALIDADO
                        && usuario.getCategoria() != null
                        && !usuario.tieneClaveGenerada();

        String mensaje;

        if (usuario.getEstado() == EstadoUsuario.PENDIENTE_VALIDACION) {
            mensaje = "Tu cuenta se encuentra en revisión.";
        } else if (usuario.getEstado() == EstadoUsuario.RECHAZADO) {
            mensaje = "Tu solicitud fue rechazada.";
        } else if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
            mensaje = "Tu cuenta se encuentra bloqueada.";
        } else if (puedeGenerarClave) {
            mensaje = "Tu cuenta fue validada. Ya podés generar tu clave personal.";
        } else {
            mensaje = "Tu cuenta ya tiene clave generada.";
        }

        return new RegistrationStatusDTO(
                usuario.getPersona().getMail(),
                usuario.getEstado().name(),
                categoria,
                puedeGenerarClave,
                mensaje
        );
    }

    public AuthResponseDTO completarRegistro(CompleteRegistrationRequestDTO request) {
        String mailNormalizado = request.getMail().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByMail(mailNormalizado)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una solicitud con ese email"
                ));

        if (usuario.getEstado() != EstadoUsuario.VALIDADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Tu cuenta todavía no fue validada por la empresa"
            );
        }

        if (usuario.getCategoria() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Tu cuenta todavía no tiene categoría asignada"
            );
        }

        if (usuario.tieneClaveGenerada()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cuenta ya tiene clave generada"
            );
        }

        validarPassword(request.getPassword(), request.getConfirmPassword());

        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuarioGuardado.getPersona().getMail());

        return toAuthResponse(usuarioGuardado, token);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        String mailNormalizado = request.getMail().trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByMail(mailNormalizado)
                .orElseThrow(InvalidCredentialsException::new);

        if (!usuario.tieneClaveGenerada()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Primero debés completar el registro y generar tu clave"
            );
        }

        if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
            throw new UserBlockedException();
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            mailNormalizado,
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(usuario.getPersona().getMail());

        return toAuthResponse(usuario, token);
    }

    private AuthResponseDTO toAuthResponse(Usuario usuario, String token) {
        String categoria = usuario.getCategoria() != null
                ? usuario.getCategoria().name()
                : null;
        boolean tieneTarjeta = tarjetaCreditoRepository.countByUsuario(usuario) > 0;
        boolean configuracionFinancieraCompleta =
                usuario.getCuenta() != null && tieneTarjeta;

        return new AuthResponseDTO(
                token,
                usuario.getIdUsuario(),
                usuario.getPersona().getNombre(),
                usuario.getPersona().getMail(),
                usuario.getRol().name(),
                usuario.getEstado().name(),
                categoria,
                tieneTarjeta,
                configuracionFinancieraCompleta
        );
    }

    private void validarPreRegister(PreRegisterRequestDTO request) {
        if (request.getNombre() == null || request.getNombre().trim().length() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre inválido");
        }

        if (request.getApellido() == null || request.getApellido().trim().length() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Apellido inválido");
        }

        if (request.getMail() == null || !request.getMail().contains("@")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email inválido");
        }

        if (request.getFrenteDNIUrl() == null || request.getFrenteDNIUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta frente del DNI");
        }

        if (request.getDorsoDNIUrl() == null || request.getDorsoDNIUrl().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta dorso del DNI");
        }

        if (request.getDomicilio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Domicilio obligatorio");
        }
    }

    private void validarPassword(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña y su confirmación son obligatorias"
            );
        }

        if (!password.equals(confirmPassword)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Las contraseñas no coinciden"
            );
        }

        if (password.length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos 8 caracteres"
            );
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos una mayúscula"
            );
        }

        if (!password.matches(".*\\d.*")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos un número"
            );
        }

        if (!password.matches(".*[^A-Za-z0-9].*")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos un carácter especial"
            );
        }
    }
}
