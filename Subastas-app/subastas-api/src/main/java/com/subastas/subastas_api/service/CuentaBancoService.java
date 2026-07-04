package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.cuenta.CuentaBancoRequestDTO;
import com.subastas.subastas_api.DTO.cuenta.CuentaBancoResponseDTO;
import com.subastas.subastas_api.model.CuentaBanco;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CuentaBancoService {

    private final UsuarioRepository usuarioRepository;

    public CuentaBancoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public CuentaBancoResponseDTO obtenerCuenta(Usuario usuario) {
        if (usuario.getCuenta() == null) {
            return null;
        }

        return toResponseDTO(usuario.getCuenta());
    }

    public CuentaBancoResponseDTO guardarOReemplazarCuenta(Usuario usuario,
                                                           CuentaBancoRequestDTO request) {
        validarCuenta(request);

        CuentaBanco cuenta = usuario.getCuenta();

        if (cuenta == null) {
            cuenta = new CuentaBanco(
                    request.getCbu().trim(),
                    request.getBanco().trim(),
                    request.getTitular().trim()
            );
        } else {
            cuenta.setCbu(request.getCbu().trim());
            cuenta.setBanco(request.getBanco().trim());
            cuenta.setTitular(request.getTitular().trim());
        }

        usuario.setCuenta(cuenta);
        usuarioRepository.save(usuario);

        return toResponseDTO(cuenta);
    }

    private void validarCuenta(CuentaBancoRequestDTO request) {
        if (request.getCbu() == null || !request.getCbu().matches("\\d{22}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El CBU/CVU debe tener 22 dígitos numéricos"
            );
        }

        if (request.getBanco() == null || request.getBanco().trim().length() < 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El banco o billetera es obligatorio"
            );
        }

        if (request.getTitular() == null || request.getTitular().trim().length() < 3) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El titular es obligatorio"
            );
        }
    }

    private CuentaBancoResponseDTO toResponseDTO(CuentaBanco cuenta) {
        return new CuentaBancoResponseDTO(
                cuenta.getIdCuentaBanco(),
                cuenta.getCbu(),
                cuenta.getBanco(),
                cuenta.getTitular()
        );
    }
}