package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.auth.AuthResponseDTO;
import com.subastas.subastas_api.DTO.auth.LoginRequestDTO;
import com.subastas.subastas_api.DTO.auth.RegisterRequestDTO;
import com.subastas.subastas_api.exception.EmailAlreadyExistsException;
import com.subastas.subastas_api.exception.InvalidCredentialsException;
import com.subastas.subastas_api.exception.UserBlockedException;
import com.subastas.subastas_api.model.EstadoUsuario;
import com.subastas.subastas_api.model.Rol;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        String mailNormalizado = request.getMail().trim().toLowerCase();

        if (usuarioRepository.existsByMail(mailNormalizado)) {
            throw new EmailAlreadyExistsException(mailNormalizado);
        }

        Usuario usuario = new Usuario(
                request.getNombre().trim(),
                request.getApellido().trim(),
                mailNormalizado,
                passwordEncoder.encode(request.getPassword()),
                Rol.USER,
                EstadoUsuario.PENDIENTE_VALIDACION,
                request.getFrenteDNIUrl(),
                request.getDorsoDNIUrl(),
                request.getDomicilio()
        );

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuarioGuardado.getMail());

        return toAuthResponse(usuarioGuardado, token);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        String mailNormalizado = request.getMail().trim().toLowerCase();

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

        Usuario usuario = usuarioRepository.findByMail(mailNormalizado)
                .orElseThrow(InvalidCredentialsException::new);

        if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
            throw new UserBlockedException();
        }

        String token = jwtService.generateToken(usuario.getMail());

        return toAuthResponse(usuario, token);
    }

    private AuthResponseDTO toAuthResponse(Usuario usuario, String token) {
        return new AuthResponseDTO(
                token,
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getMail(),
                usuario.getRol().name(),
                usuario.getEstado().name()
        );
    }
}