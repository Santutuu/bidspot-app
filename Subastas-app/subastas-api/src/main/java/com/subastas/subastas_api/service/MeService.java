package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.cuenta.CuentaCobroRequestDTO;
import com.subastas.subastas_api.DTO.cuenta.CuentaCobroResponseDTO;
import com.subastas.subastas_api.DTO.mediosPago.*;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.ChequeRepository;
import com.subastas.subastas_api.repository.TarjetaCreditoRepository;
import com.subastas.subastas_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class MeService {

    private final UsuarioRepository usuarioRepository;
    private final TarjetaCreditoRepository tarjetaCreditoRepository;
    private final ChequeRepository chequeRepository;

    public MeService(UsuarioRepository usuarioRepository,
                     TarjetaCreditoRepository tarjetaCreditoRepository,
                     ChequeRepository chequeRepository) {
        this.usuarioRepository = usuarioRepository;
        this.tarjetaCreditoRepository = tarjetaCreditoRepository;
        this.chequeRepository = chequeRepository;
    }

    public CuentaCobroResponseDTO obtenerCuentaCobro(String mail) {
        Usuario usuario = obtenerUsuario(mail);

        if (usuario.getCuenta() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Todavía no cargaste una cuenta de cobro"
            );
        }

        CuentaBanco cuenta = usuario.getCuenta();

        return new CuentaCobroResponseDTO(
                cuenta.getIdCuentaBanco(),
                cuenta.getCbu(),
                cuenta.getBanco(),
                cuenta.getTitular()
        );
    }

    public CuentaCobroResponseDTO crearCuentaCobro(String mail,
                                                   CuentaCobroRequestDTO request) {
        Usuario usuario = obtenerUsuario(mail);

        validarCuentaCobro(request);

        CuentaBanco cuenta = new CuentaBanco(
                request.getCbu(),
                request.getBanco().trim(),
                request.getTitular().trim()
        );

        usuario.setCuenta(cuenta);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        CuentaBanco cuentaGuardada = usuarioGuardado.getCuenta();

        return new CuentaCobroResponseDTO(
                cuentaGuardada.getIdCuentaBanco(),
                cuentaGuardada.getCbu(),
                cuentaGuardada.getBanco(),
                cuentaGuardada.getTitular()
        );
    }

    public List<MedioPagoResponseDTO> obtenerMediosPago(String mail) {
        Usuario usuario = obtenerUsuario(mail);

        List<MedioPagoResponseDTO> response = new ArrayList<>();

        List<TarjetaCredito> tarjetas = tarjetaCreditoRepository.findByUsuario(usuario);
        List<Cheque> cheques = chequeRepository.findByUsuario(usuario);

        for (TarjetaCredito tarjeta : tarjetas) {
            response.add(new MedioPagoResponseDTO(
                    tarjeta.getIdMedioPago(),
                    "TARJETA",
                    "Tarjeta terminada en " + ultimosDigitos(tarjeta.getNumero())
            ));
        }

        for (Cheque cheque : cheques) {
            response.add(new MedioPagoResponseDTO(
                    cheque.getIdMedioPago(),
                    "CHEQUE",
                    "Cheque #" + cheque.getNroCheque() + " - $" + cheque.getSaldo()
            ));
        }

        return response;
    }

    public List<TarjetaResponseDTO> obtenerTarjetas(String mail) {
        Usuario usuario = obtenerUsuario(mail);

        return tarjetaCreditoRepository.findByUsuario(usuario)
                .stream()
                .map(t -> new TarjetaResponseDTO(
                        t.getIdMedioPago(),
                        t.getNumero(),
                        t.getNombre(),
                        t.getFechaVto()
                ))
                .toList();
    }

    public List<ChequeResponseDTO> obtenerCheques(String mail) {
        Usuario usuario = obtenerUsuario(mail);

        return chequeRepository.findByUsuario(usuario)
                .stream()
                .map(c -> new ChequeResponseDTO(
                        c.getIdMedioPago(),
                        c.getIdentificacion(),
                        c.getNroCheque(),
                        c.getBeneficiario(),
                        c.getCuilCuit(),
                        c.getSaldo()
                ))
                .toList();
    }

    public TarjetaResponseDTO crearTarjeta(String mail,
                                           TarjetaRequestDTO request) {
        Usuario usuario = obtenerUsuario(mail);

        validarTarjeta(request);

        TarjetaCredito tarjeta = new TarjetaCredito(
                usuario,
                request.getNumero(),
                request.getNombre().trim(),
                request.getFechaVto(),
                request.getCvv()

        );

        TarjetaCredito guardada = tarjetaCreditoRepository.save(tarjeta);

        return new TarjetaResponseDTO(
                guardada.getIdMedioPago(),
                guardada.getNumero(),
                guardada.getNombre(),
                guardada.getFechaVto()
        );
    }

    public ChequeResponseDTO crearCheque(String mail,
                                         ChequeRequestDTO request) {
        Usuario usuario = obtenerUsuario(mail);

        validarCheque(request);

        Cheque cheque = new Cheque(
                usuario,
                request.getIdentificacion(),
                request.getNroCheque(),
                request.getBeneficiario().trim(),
                request.getCuilCuit(),
                request.getSaldo()
        );

        Cheque guardado = chequeRepository.save(cheque);

        return new ChequeResponseDTO(
                guardado.getIdMedioPago(),
                guardado.getIdentificacion(),
                guardado.getNroCheque(),
                guardado.getBeneficiario(),
                guardado.getCuilCuit(),
                guardado.getSaldo()
        );
    }

    private Usuario obtenerUsuario(String mail) {
        return usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no autenticado"
                ));
    }

    private void validarCuentaCobro(CuentaCobroRequestDTO request) {
        if (request.getCbu() == null && !request.getCbu().trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El CBU es obligatorio"
            );
        }

        if (request.getBanco() == null || request.getBanco().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El banco o billetera es obligatorio"
            );
        }

        if (request.getTitular() == null || request.getTitular().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El titular es obligatorio"
            );
        }
    }

    private void validarTarjeta(TarjetaRequestDTO request) {
        if (request.getNumero() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El número de tarjeta es obligatorio"
            );
        }

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre del titular es obligatorio"
            );
        }

        if (request.getFechaVto() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de vencimiento es obligatoria"
            );
        }

        if (request.getCvv() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El CVV es obligatorio"
            );
        }
    }

    private void validarCheque(ChequeRequestDTO request) {
        if (request.getIdentificacion() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La identificación es obligatoria"
            );
        }

        if (request.getNroCheque() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El número de cheque es obligatorio"
            );
        }

        if (request.getBeneficiario() == null || request.getBeneficiario().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El beneficiario es obligatorio"
            );
        }

        if (request.getCuilCuit() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El CUIL/CUIT es obligatorio"
            );
        }

        if (request.getSaldo() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El saldo del cheque debe ser mayor a cero"
            );
        }
    }

    private String ultimosDigitos(String numero) {
        if (numero == null || numero.length() < 4) {
            return numero;
        }

        return numero.substring(numero.length() - 4);
    }
}