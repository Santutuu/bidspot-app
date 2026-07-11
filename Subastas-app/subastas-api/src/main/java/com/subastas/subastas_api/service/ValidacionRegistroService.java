package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.admin.AprobacionUsuarioResponseDTO;
import com.subastas.subastas_api.DTO.admin.AprobarUsuarioRequestDTO;
import com.subastas.subastas_api.model.*;
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
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
    }

    @Transactional
    public AprobacionUsuarioResponseDTO aprobarUsuario(
            Long idUsuario,
            AprobarUsuarioRequestDTO request
    ) {
        validarRequest(request);

        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un usuario con id " + idUsuario
                ));

        if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La cuenta se encuentra bloqueada"
            );
        }

        if (usuario.getEstado() == EstadoUsuario.RECHAZADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La solicitud fue rechazada anteriormente"
            );
        }

        Persona persona = usuario.getPersona();

        if (persona == null || persona.getIdPersona() == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario no tiene una persona válida asociada"
            );
        }

        Empleado verificador = empleadoRepository
                .findById(request.getIdVerificador())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe el empleado verificador con id "
                                + request.getIdVerificador()
                ));

        /*
         * En legacy:
         *
         * clientes.identificador = personas.identificador
         */
        Cliente cliente = clienteRepository
                .findById(persona.getIdPersona())
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

        /*
         * Puente temporal.
         *
         * Se mantiene hasta eliminar usuario.cliente_legacy_id.
         */
        usuario.setClienteLegacy(clienteGuardado);

        /*
         * También se mantienen temporalmente sincronizados los campos
         * modernos de Usuario para no romper servicios antiguos.
         */
        usuario.setEstado(EstadoUsuario.VALIDADO);
        usuario.setCategoria(request.getCategoria());

        usuarioRepository.save(usuario);

        return new AprobacionUsuarioResponseDTO(
                usuario.getIdUsuario(),
                persona.getIdPersona(),
                clienteGuardado.getIdentificador(),
                persona.getMail(),
                usuario.getEstadoEfectivo().name(),
                clienteGuardado.getAdmitido(),
                clienteGuardado.getCategoria() != null
                        ? clienteGuardado.getCategoria().name()
                        : null
        );
    }

    @Transactional
    public AprobacionUsuarioResponseDTO rechazarUsuario(Long idUsuario) {
        Usuario usuario = usuarioRepository
                .findById(idUsuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un usuario con id " + idUsuario
                ));

        if (usuario.getEstado() == EstadoUsuario.VALIDADO
                || usuario.estaValidadoComoCliente()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede rechazar una solicitud ya aprobada"
            );
        }

        usuario.setEstado(EstadoUsuario.RECHAZADO);
        usuarioRepository.save(usuario);

        return new AprobacionUsuarioResponseDTO(
                usuario.getIdUsuario(),
                usuario.getPersona().getIdPersona(),
                null,
                usuario.getPersona().getMail(),
                usuario.getEstadoEfectivo().name(),
                "no",
                null
        );
    }

    private void validarRequest(AprobarUsuarioRequestDTO request) {
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