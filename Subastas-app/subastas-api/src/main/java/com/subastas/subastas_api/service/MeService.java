package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.cuenta.CuentaBancoRequestDTO;
import com.subastas.subastas_api.DTO.cuenta.CuentaBancoResponseDTO;
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

    private static final int MAX_TARJETAS = 3;
    private static final int MAX_CHEQUES = 3;

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

    public CuentaBancoResponseDTO obtenerCuentaCobro(String mail) {
        Usuario usuario = obtenerUsuario(mail);

        if (usuario.getCuenta() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Todavía no cargaste una cuenta de cobro"
            );
        }

        CuentaBanco cuenta = usuario.getCuenta();

        return new CuentaBancoResponseDTO(
                cuenta.getIdCuentaBanco(),
                cuenta.getCbu(),
                cuenta.getBanco(),
                cuenta.getTitular()
        );
    }

    public CuentaBancoResponseDTO crearCuentaCobro(String mail,
                                                   CuentaBancoRequestDTO request) {
        Usuario usuario = obtenerUsuario(mail);

        validarCuentaCobro(request);

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
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        CuentaBanco cuentaGuardada = usuarioGuardado.getCuenta();

        return new CuentaBancoResponseDTO(
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
                    "Tarjeta " + enmascararNumero(tarjeta.getNumero())
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
                        enmascararNumero(t.getNumero()),
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

        if (tarjetaCreditoRepository.countByUsuario(usuario) >= MAX_TARJETAS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden cargar hasta 3 tarjetas"
            );
        }

        validarTarjeta(request);

        TarjetaCredito tarjeta = new TarjetaCredito(
                usuario,
                request.getNumero().trim(),
                request.getNombre().trim(),
                request.getFechaVto().trim(),
                request.getCvv().trim()
        );

        TarjetaCredito guardada = tarjetaCreditoRepository.save(tarjeta);

        return new TarjetaResponseDTO(
                guardada.getIdMedioPago(),
                enmascararNumero(guardada.getNumero()),
                guardada.getNombre(),
                guardada.getFechaVto()
        );
    }

    public ChequeResponseDTO crearCheque(String mail,
                                         ChequeRequestDTO request) {
        Usuario usuario = obtenerUsuario(mail);

        if (chequeRepository.countByUsuario(usuario) >= MAX_CHEQUES) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden cargar hasta 3 cheques"
            );
        }

        validarCheque(request);

        Cheque cheque = new Cheque(
                usuario,
                request.getIdentificacion().trim(),
                request.getNroCheque().trim(),
                request.getBeneficiario().trim(),
                request.getCuilCuit().trim(),
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

    public void eliminarTarjeta(String mail, Long idTarjeta) {
        Usuario usuario = obtenerUsuario(mail);

        TarjetaCredito tarjeta = tarjetaCreditoRepository
                .findByIdMedioPagoAndUsuario(idTarjeta, usuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una tarjeta con ese id"
                ));

        tarjetaCreditoRepository.delete(tarjeta);
    }

    public void eliminarCheque(String mail, Long idCheque) {
        Usuario usuario = obtenerUsuario(mail);

        Cheque cheque = chequeRepository
                .findByIdMedioPagoAndUsuario(idCheque, usuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un cheque con ese id"
                ));

        chequeRepository.delete(cheque);
    }

    private Usuario obtenerUsuario(String mail) {
        return usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no autenticado"
                ));
    }

    private void validarCuentaCobro(CuentaBancoRequestDTO request) {
        if (request.getCbu() == null || !request.getCbu().trim().matches("\\d{22}")) {
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

    private void validarTarjeta(TarjetaRequestDTO request) {
        if (request.getNumero() == null || !request.getNumero().trim().matches("\\d{13,19}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El número de tarjeta debe tener entre 13 y 19 dígitos"
            );
        }

        if (request.getNombre() == null || request.getNombre().trim().length() < 3) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre del titular es obligatorio"
            );
        }

        if (request.getFechaVto() == null ||
                !request.getFechaVto().trim().matches("(0[1-9]|1[0-2])/(\\d{2}|\\d{4})")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de vencimiento debe tener formato MM/AA o MM/AAAA"
            );
        }

        if (request.getCvv() == null || !request.getCvv().trim().matches("\\d{3,4}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El CVV debe tener 3 o 4 dígitos"
            );
        }
    }

    private void validarCheque(ChequeRequestDTO request) {
        if (request.getIdentificacion() == null || request.getIdentificacion().trim().length() < 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La identificación es obligatoria"
            );
        }

        if (request.getNroCheque() == null || !request.getNroCheque().trim().matches("\\d+")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El número de cheque es obligatorio"
            );
        }

        if (request.getBeneficiario() == null || request.getBeneficiario().trim().length() < 3) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El beneficiario es obligatorio"
            );
        }

        if (request.getCuilCuit() == null || !request.getCuilCuit().trim().matches("\\d{11}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El CUIL/CUIT debe tener 11 dígitos"
            );
        }

        if (request.getSaldo() == null || request.getSaldo() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El saldo del cheque debe ser mayor a cero"
            );
        }
    }

    private String enmascararNumero(String numero) {
        if (numero == null || numero.length() < 4) {
            return "****";
        }

        return "**** " + numero.substring(numero.length() - 4);
    }
}