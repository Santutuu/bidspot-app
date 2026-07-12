package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.auth.AuthResponseDTO;
import com.subastas.subastas_api.DTO.auth.CompleteRegistrationRequestDTO;
import com.subastas.subastas_api.DTO.auth.LoginRequestDTO;
import com.subastas.subastas_api.DTO.auth.PreRegisterRequestDTO;
import com.subastas.subastas_api.DTO.auth.PreRegisterResponseDTO;
import com.subastas.subastas_api.DTO.auth.RegistrationStatusDTO;
import com.subastas.subastas_api.exception.EmailAlreadyExistsException;
import com.subastas.subastas_api.exception.InvalidCredentialsException;
import com.subastas.subastas_api.exception.UserBlockedException;
import com.subastas.subastas_api.model.CategoriaUsuario;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.DocumentoPersona;
import com.subastas.subastas_api.model.EstadoRegistro;
import com.subastas.subastas_api.model.EstadoUsuario;
import com.subastas.subastas_api.model.Persona;
import com.subastas.subastas_api.model.Rol;
import com.subastas.subastas_api.model.TipoDocumentoPersona;
import com.subastas.subastas_api.model.Usuario;
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

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            EmailService emailService,
            TarjetaCreditoRepository tarjetaCreditoRepository
    ) {
        this.usuarioRepository =
                usuarioRepository;

        this.passwordEncoder =
                passwordEncoder;

        this.jwtService =
                jwtService;

        this.authenticationManager =
                authenticationManager;

        this.emailService =
                emailService;

        this.tarjetaCreditoRepository =
                tarjetaCreditoRepository;
    }

    public PreRegisterResponseDTO preRegister(
            PreRegisterRequestDTO request
    ) {
        validarPreRegister(request);

        String mailNormalizado =
                request.getMail()
                        .trim()
                        .toLowerCase();

        if (usuarioRepository
                .existsByMail(mailNormalizado)) {

            throw new EmailAlreadyExistsException(
                    mailNormalizado
            );
        }

        Persona persona = new Persona(
                request.getNombre().trim(),
                request.getApellido().trim(),
                request.getDocumento().trim(),
                mailNormalizado,
                request.getDomicilio()
        );

        DocumentoPersona frenteDni =
                new DocumentoPersona(
                        persona,
                        TipoDocumentoPersona.DNI_FRENTE,
                        request.getFrenteDNIUrl()
                );

        DocumentoPersona dorsoDni =
                new DocumentoPersona(
                        persona,
                        TipoDocumentoPersona.DNI_DORSO,
                        request.getDorsoDNIUrl()
                );

        persona.agregarDocumento(frenteDni);
        persona.agregarDocumento(dorsoDni);
        persona.marcarRegistroPendiente();

        Usuario usuario = new Usuario(
                persona,
                null,
                Rol.USER,
                EstadoUsuario.ACTIVO
        );

        persona.setUsuario(usuario);

        Usuario usuarioGuardado =
                usuarioRepository.save(usuario);

        emailService.enviarSolicitudRecibida(
                usuarioGuardado
                        .getPersona()
                        .getMail(),
                usuarioGuardado
                        .getPersona()
                        .getNombre()
        );

        return new PreRegisterResponseDTO(
                usuarioGuardado.getIdUsuario(),
                usuarioGuardado
                        .getPersona()
                        .getNombre(),
                usuarioGuardado
                        .getPersona()
                        .getMail(),
                usuarioGuardado.getEstadoExpuesto(),
                "Tu solicitud fue enviada y se encuentra pendiente de validación."
        );
    }

    public RegistrationStatusDTO obtenerEstadoRegistro(
            String mail
    ) {
        String mailNormalizado =
                normalizarMail(mail);

        Usuario usuario = usuarioRepository
                .findByMail(mailNormalizado)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe una solicitud con ese email"
                        )
                );

        CategoriaUsuario categoriaNegocio =
                usuario.getCategoriaNegocio();

        String categoria =
                categoriaNegocio != null
                        ? categoriaNegocio.name()
                        : null;

        EstadoRegistro estadoRegistro =
                usuario.getEstadoRegistro();

        boolean puedeGenerarClave =
                !usuario.estaBloqueado()
                        && estadoRegistro
                        == EstadoRegistro.VALIDADO
                        && usuario.estaValidadoComoCliente()
                        && categoriaNegocio != null
                        && !usuario.tieneClaveGenerada();

        String mensaje;

        if (usuario.estaBloqueado()) {
            mensaje =
                    "Tu cuenta se encuentra bloqueada.";

        } else if (estadoRegistro
                == EstadoRegistro.RECHAZADO) {

            mensaje =
                    "Tu solicitud fue rechazada.";

        } else if (estadoRegistro
                == EstadoRegistro.PENDIENTE_VALIDACION) {

            mensaje =
                    "Tu cuenta se encuentra en revisión.";

        } else if (puedeGenerarClave) {

            mensaje =
                    "Tu cuenta fue validada. Ya podés generar tu clave personal.";

        } else {

            mensaje =
                    "Tu cuenta ya tiene clave generada.";
        }

        return new RegistrationStatusDTO(
                usuario.getPersona().getMail(),
                usuario.getEstadoExpuesto(),
                categoria,
                puedeGenerarClave,
                mensaje
        );
    }

    public AuthResponseDTO completarRegistro(
            CompleteRegistrationRequestDTO request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos del registro son obligatorios"
            );
        }

        String mailNormalizado =
                normalizarMail(request.getMail());

        Usuario usuario = usuarioRepository
                .findByMail(mailNormalizado)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe una solicitud con ese email"
                        )
                );

        if (usuario.estaBloqueado()) {
            throw new UserBlockedException();
        }

        if (usuario.getEstadoRegistro()
                != EstadoRegistro.VALIDADO) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Tu cuenta todavía no fue validada por la empresa"
            );
        }

        Cliente cliente =
                usuario.getCliente();

        if (cliente == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Todavía no se creó el perfil de cliente"
            );
        }

        if (!cliente.estaAdmitido()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El cliente todavía no fue admitido por la empresa"
            );
        }

        if (cliente.getCategoria() == null) {
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

        validarPassword(
                request.getPassword(),
                request.getConfirmPassword()
        );

        usuario.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        Usuario usuarioGuardado =
                usuarioRepository.save(usuario);

        String token =
                jwtService.generateToken(
                        usuarioGuardado
                                .getPersona()
                                .getMail()
                );

        return toAuthResponse(
                usuarioGuardado,
                token
        );
    }

    public AuthResponseDTO login(
            LoginRequestDTO request
    ) {
        if (request == null) {
            throw new InvalidCredentialsException();
        }

        String mailNormalizado =
                normalizarMail(request.getMail());

        Usuario usuario = usuarioRepository
                .findByMail(mailNormalizado)
                .orElseThrow(
                        InvalidCredentialsException::new
                );

        if (usuario.estaBloqueado()) {
            throw new UserBlockedException();
        }

        if (!usuario.tieneClaveGenerada()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Primero debés completar el registro y generar tu clave"
            );
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            mailNormalizado,
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException exception) {
            throw new InvalidCredentialsException();
        }

        String token =
                jwtService.generateToken(
                        usuario.getPersona()
                                .getMail()
                );

        return toAuthResponse(
                usuario,
                token
        );
    }

    private AuthResponseDTO toAuthResponse(
            Usuario usuario,
            String token
    ) {
        CategoriaUsuario categoriaNegocio =
                usuario.getCategoriaNegocio();

        String categoria =
                categoriaNegocio != null
                        ? categoriaNegocio.name()
                        : null;

        Cliente cliente =
                usuario.getCliente();

        boolean tieneTarjeta =
                cliente != null
                        && tarjetaCreditoRepository
                        .countByCliente(cliente) > 0;

        boolean configuracionFinancieraCompleta =
                cliente != null
                        && cliente.getCuenta() != null
                        && tieneTarjeta;

        return new AuthResponseDTO(
                token,
                usuario.getIdUsuario(),
                usuario.getPersona().getNombre(),
                usuario.getPersona().getMail(),
                usuario.getRol().name(),
                usuario.getEstadoExpuesto(),
                categoria,
                tieneTarjeta,
                configuracionFinancieraCompleta
        );
    }

    private void validarPreRegister(
            PreRegisterRequestDTO request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos del registro son obligatorios"
            );
        }

        if (request.getNombre() == null
                || request.getNombre()
                .trim()
                .length() < 2) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nombre inválido"
            );
        }

        if (request.getApellido() == null
                || request.getApellido()
                .trim()
                .length() < 2) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Apellido inválido"
            );
        }

        if (request.getDocumento() == null
                || request.getDocumento()
                .trim()
                .isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El número de documento es obligatorio"
            );
        }

        if (request.getMail() == null
                || !request.getMail().contains("@")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email inválido"
            );
        }

        if (request.getFrenteDNIUrl() == null
                || request.getFrenteDNIUrl()
                .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Falta frente del DNI"
            );
        }

        if (request.getDorsoDNIUrl() == null
                || request.getDorsoDNIUrl()
                .isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Falta dorso del DNI"
            );
        }

        if (request.getDomicilio() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Domicilio obligatorio"
            );
        }
    }

    private void validarPassword(
            String password,
            String confirmPassword
    ) {
        if (password == null
                || confirmPassword == null) {

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

        if (!password.matches(
                ".*[^A-Za-z0-9].*"
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos un carácter especial"
            );
        }
    }

    private String normalizarMail(
            String mail
    ) {
        if (mail == null
                || mail.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El email es obligatorio"
            );
        }

        return mail
                .trim()
                .toLowerCase();
    }
}