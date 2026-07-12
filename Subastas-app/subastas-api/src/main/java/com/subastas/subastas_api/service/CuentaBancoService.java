package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.cuenta.CuentaBancoRequestDTO;
import com.subastas.subastas_api.DTO.cuenta.CuentaBancoResponseDTO;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.CuentaBanco;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CuentaBancoService {

    private final ClienteRepository clienteRepository;

    public CuentaBancoService(
            ClienteRepository clienteRepository
    ) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public CuentaBancoResponseDTO obtenerCuenta(
            Usuario usuario
    ) {
        Cliente cliente = obtenerClienteValido(usuario);

        if (cliente.getCuenta() == null) {
            return null;
        }

        return toResponseDTO(
                cliente.getCuenta()
        );
    }

    @Transactional
    public CuentaBancoResponseDTO guardarOReemplazarCuenta(
            Usuario usuario,
            CuentaBancoRequestDTO request
    ) {
        Cliente cliente = obtenerClienteValido(usuario);

        validarCuenta(request);

        CuentaBanco cuenta = cliente.getCuenta();

        if (cuenta == null) {
            cuenta = new CuentaBanco(
                    request.getCbu().trim(),
                    request.getBanco().trim(),
                    request.getTitular().trim()
            );
        } else {
            cuenta.setCbu(
                    request.getCbu().trim()
            );

            cuenta.setBanco(
                    request.getBanco().trim()
            );

            cuenta.setTitular(
                    request.getTitular().trim()
            );
        }

        cliente.setCuenta(cuenta);

        Cliente clienteGuardado =
                clienteRepository.save(cliente);

        return toResponseDTO(
                clienteGuardado.getCuenta()
        );
    }

    private Cliente obtenerClienteValido(
            Usuario usuario
    ) {
        if (usuario == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        Cliente cliente = usuario.getCliente();

        if (cliente == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El usuario no tiene un perfil de cliente asociado"
            );
        }

        if (!cliente.estaAdmitido()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El cliente no está admitido"
            );
        }

        return cliente;
    }

    private void validarCuenta(
            CuentaBancoRequestDTO request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de la cuenta son obligatorios"
            );
        }

        if (request.getCbu() == null
                || !request.getCbu()
                .trim()
                .matches("\\d{22}")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El CBU/CVU debe tener 22 dígitos numéricos"
            );
        }

        if (request.getBanco() == null
                || request.getBanco()
                .trim()
                .length() < 2) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El banco o billetera es obligatorio"
            );
        }

        if (request.getTitular() == null
                || request.getTitular()
                .trim()
                .length() < 3) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El titular es obligatorio"
            );
        }
    }

    private CuentaBancoResponseDTO toResponseDTO(
            CuentaBanco cuenta
    ) {
        return new CuentaBancoResponseDTO(
                cuenta.getIdCuentaBanco(),
                cuenta.getCbu(),
                cuenta.getBanco(),
                cuenta.getTitular()
        );
    }
}