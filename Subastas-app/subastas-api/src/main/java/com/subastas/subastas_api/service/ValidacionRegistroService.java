package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.admin.AprobacionUsuarioResponseDTO;
import com.subastas.subastas_api.DTO.admin.AprobarUsuarioRequestDTO;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.Empleado;
import com.subastas.subastas_api.model.EstadoRegistro;
import com.subastas.subastas_api.model.Persona;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.ClienteRepository;
import com.subastas.subastas_api.repository.EmpleadoRepository;
import com.subastas.subastas_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ValidacionRegistroService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;

    public ValidacionRegistroService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            EmpleadoRepository empleadoRepository
    ) {
        this.usuarioRepository =
                usuarioRepository;

        this.clienteRepository =
                clienteRepository;

        this.empleadoRepository =
                empleadoRepository;
    }

    @Transactional
    public AprobacionUsuarioResponseDTO aprobarUsuario(
            Long idUsuario,
            AprobarUsuarioRequestDTO request
    ) {
        validarRequest(request);

        Usuario usuario =
                obtenerUsuario(idUsuario);

        if (usuario.estaBloqueado()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cuenta se encuentra bloqueada"
            );
        }

        Persona persona =
                obtenerPersonaValida(usuario);

        if (persona.getEstadoRegistro()
                == EstadoRegistro.RECHAZADO) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud fue rechazada anteriormente"
            );
        }

        Empleado verificador =
                empleadoRepository
                        .findById(
                                request.getIdVerificador()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No existe el empleado verificador con id "
                                                + request.getIdVerificador()
                                )
                        );

        Cliente cliente =
                clienteRepository
                        .findById(
                                persona.getIdPersona()
                        )
                        .orElse(null);

        if (cliente == null) {
            cliente = new Cliente(
                    persona,
                    request.getCategoria(),
                    verificador
            );
        } else {
            cliente.aprobar(
                    request.getCategoria(),
                    verificador
            );
        }

        Cliente clienteGuardado =
                clienteRepository.save(cliente);

        persona.aprobarRegistro();

        /*
         * El usuario debe quedar técnicamente activo.
         */
        usuario.activar();

        usuarioRepository.save(usuario);

        return new AprobacionUsuarioResponseDTO(
                usuario.getIdUsuario(),
                persona.getIdPersona(),
                clienteGuardado.getIdentificador(),
                persona.getMail(),
                usuario.getEstadoExpuesto(),
                clienteGuardado.getAdmitido(),
                clienteGuardado.getCategoria() != null
                        ? clienteGuardado
                        .getCategoria()
                        .name()
                        : null
        );
    }

    @Transactional
    public AprobacionUsuarioResponseDTO rechazarUsuario(
            Long idUsuario
    ) {
        Usuario usuario =
                obtenerUsuario(idUsuario);

        Persona persona =
                obtenerPersonaValida(usuario);

        if (persona.getEstadoRegistro()
                == EstadoRegistro.VALIDADO
                || usuario.estaValidadoComoCliente()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede rechazar una solicitud ya aprobada"
            );
        }

        persona.rechazarRegistro();

        usuarioRepository.save(usuario);

        return new AprobacionUsuarioResponseDTO(
                usuario.getIdUsuario(),
                persona.getIdPersona(),
                null,
                persona.getMail(),
                usuario.getEstadoExpuesto(),
                "no",
                null
        );
    }

    private Usuario obtenerUsuario(
            Long idUsuario
    ) {
        return usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe un usuario con id "
                                        + idUsuario
                        )
                );
    }

    private Persona obtenerPersonaValida(
            Usuario usuario
    ) {
        Persona persona =
                usuario.getPersona();

        if (persona == null
                || persona.getIdPersona() == null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario no tiene una persona válida asociada"
            );
        }

        return persona;
    }

    private void validarRequest(
            AprobarUsuarioRequestDTO request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de aprobación son obligatorios"
            );
        }

        if (request.getCategoria() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La categoría es obligatoria"
            );
        }

        if (request.getIdVerificador() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El empleado verificador es obligatorio"
            );
        }
    }
}