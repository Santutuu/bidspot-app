package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.cuenta.CuentaBancoRequestDTO;
import com.subastas.subastas_api.DTO.cuenta.CuentaBancoResponseDTO;
import com.subastas.subastas_api.DTO.mediosPago.*;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.ChequeRepository;
import com.subastas.subastas_api.repository.ClienteRepository;
import com.subastas.subastas_api.repository.TarjetaCreditoRepository;
import com.subastas.subastas_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class MeService {

    private static final int MAX_TARJETAS = 3;
    private static final int MAX_CHEQUES = 3;

    private static final float LIMITE_TARJETA_ARS = 300000f;
    private static final float LIMITE_TARJETA_DOLARES = 30000f;

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final TarjetaCreditoRepository tarjetaCreditoRepository;
    private final ChequeRepository chequeRepository;

    public MeService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            TarjetaCreditoRepository tarjetaCreditoRepository,
            ChequeRepository chequeRepository
    ) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.tarjetaCreditoRepository = tarjetaCreditoRepository;
        this.chequeRepository = chequeRepository;
    }

    @Transactional(readOnly = true)
    public CuentaBancoResponseDTO obtenerCuentaCobro(
            String mail
    ) {
        Cliente cliente = obtenerCliente(mail);

        if (cliente.getCuenta() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Todavía no cargaste una cuenta de cobro"
            );
        }

        CuentaBanco cuenta = cliente.getCuenta();

        return new CuentaBancoResponseDTO(
                cuenta.getIdCuentaBanco(),
                cuenta.getCbu(),
                cuenta.getBanco(),
                cuenta.getTitular()
        );
    }

    @Transactional
    public CuentaBancoResponseDTO crearCuentaCobro(
            String mail,
            CuentaBancoRequestDTO request
    ) {
        Cliente cliente = obtenerCliente(mail);

        validarCuentaCobro(request);

        CuentaBanco cuenta = cliente.getCuenta();

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

        cliente.setCuenta(cuenta);

        Cliente clienteGuardado =
                clienteRepository.save(cliente);

        CuentaBanco cuentaGuardada =
                clienteGuardado.getCuenta();

        return new CuentaBancoResponseDTO(
                cuentaGuardada.getIdCuentaBanco(),
                cuentaGuardada.getCbu(),
                cuentaGuardada.getBanco(),
                cuentaGuardada.getTitular()
        );
    }

    @Transactional(readOnly = true)
    public List<MedioPagoResponseDTO> obtenerMediosPago(
            String mail
    ) {
        Cliente cliente = obtenerCliente(mail);

        List<MedioPagoResponseDTO> response =
                new ArrayList<>();

        List<TarjetaCredito> tarjetas =
                tarjetaCreditoRepository.findByCliente(cliente);

        List<Cheque> cheques =
                chequeRepository.findByCliente(cliente);

        for (TarjetaCredito tarjeta : tarjetas) {
            response.add(new MedioPagoResponseDTO(
                    tarjeta.getIdMedioPago(),
                    "TARJETA",
                    "Tarjeta "
                            + enmascararNumero(tarjeta.getNumero()),
                    tarjeta.getMoneda().name(),
                    tarjeta.getLimiteCredito()
            ));
        }

        for (Cheque cheque : cheques) {
            response.add(new MedioPagoResponseDTO(
                    cheque.getIdMedioPago(),
                    "CHEQUE",
                    "Cheque #"
                            + cheque.getNroCheque()
                            + " - "
                            + cheque.getSaldo(),
                    cheque.getMoneda().name(),
                    cheque.getSaldo()
            ));
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<TarjetaResponseDTO> obtenerTarjetas(
            String mail
    ) {
        Cliente cliente = obtenerCliente(mail);

        return tarjetaCreditoRepository
                .findByCliente(cliente)
                .stream()
                .map(this::toTarjetaResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChequeResponseDTO> obtenerCheques(
            String mail
    ) {
        Cliente cliente = obtenerCliente(mail);

        return chequeRepository
                .findByCliente(cliente)
                .stream()
                .map(this::toChequeResponseDTO)
                .toList();
    }

    @Transactional
    public TarjetaResponseDTO crearTarjeta(
            String mail,
            TarjetaRequestDTO request
    ) {
        Cliente cliente = obtenerCliente(mail);

        if (tarjetaCreditoRepository.countByCliente(cliente)
                >= MAX_TARJETAS) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden cargar hasta 3 tarjetas"
            );
        }

        validarTarjeta(request);

        Float limiteCredito =
                obtenerLimiteCreditoTarjeta(request.getMoneda());

        TarjetaCredito tarjeta = new TarjetaCredito(
                cliente,
                request.getNumero().trim(),
                request.getNombre().trim(),
                request.getFechaVto().trim(),
                request.getCvv().trim(),
                request.getMoneda(),
                limiteCredito
        );

        TarjetaCredito guardada =
                tarjetaCreditoRepository.save(tarjeta);

        return toTarjetaResponseDTO(guardada);
    }

    @Transactional
    public ChequeResponseDTO crearCheque(
            String mail,
            ChequeRequestDTO request
    ) {
        Cliente cliente = obtenerCliente(mail);

        if (chequeRepository.countByCliente(cliente)
                >= MAX_CHEQUES) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Solo se pueden cargar hasta 3 cheques"
            );
        }

        validarCheque(request);

        Cheque cheque = new Cheque(
                cliente,
                request.getIdentificacion().trim(),
                request.getNroCheque().trim(),
                request.getBeneficiario().trim(),
                request.getCuilCuit().trim(),
                request.getSaldo(),
                request.getMoneda()
        );

        Cheque guardado =
                chequeRepository.save(cheque);

        return toChequeResponseDTO(guardado);
    }

    @Transactional
    public void eliminarTarjeta(
            String mail,
            Long idTarjeta
    ) {
        Cliente cliente = obtenerCliente(mail);

        TarjetaCredito tarjeta =
                tarjetaCreditoRepository
                        .findByIdMedioPagoAndCliente(
                                idTarjeta,
                                cliente
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No existe una tarjeta con ese id"
                                )
                        );

        tarjetaCreditoRepository.delete(tarjeta);
    }

    @Transactional
    public void eliminarCheque(
            String mail,
            Long idCheque
    ) {
        Cliente cliente = obtenerCliente(mail);

        Cheque cheque =
                chequeRepository
                        .findByIdMedioPagoAndCliente(
                                idCheque,
                                cliente
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No existe un cheque con ese id"
                                )
                        );

        chequeRepository.delete(cheque);
    }

    private Usuario obtenerUsuario(String mail) {
        if (mail == null || mail.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        return usuarioRepository
                .findByMail(mail.trim().toLowerCase())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.UNAUTHORIZED,
                                "Usuario no autenticado"
                        )
                );
    }

    private Cliente obtenerCliente(String mail) {
        Usuario usuario = obtenerUsuario(mail);
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

    private void validarCuentaCobro(
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
                || request.getBanco().trim().length() < 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El banco o billetera es obligatorio"
            );
        }

        if (request.getTitular() == null
                || request.getTitular().trim().length() < 3) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El titular es obligatorio"
            );
        }
    }

    private void validarTarjeta(
            TarjetaRequestDTO request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de la tarjeta son obligatorios"
            );
        }

        if (request.getNumero() == null
                || !request.getNumero()
                .trim()
                .matches("\\d{13,19}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El número de tarjeta debe tener entre 13 y 19 dígitos"
            );
        }

        if (request.getNombre() == null
                || request.getNombre().trim().length() < 3) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre del titular es obligatorio"
            );
        }

        if (request.getFechaVto() == null
                || !request.getFechaVto()
                .trim()
                .matches("(0[1-9]|1[0-2])/(\\d{2}|\\d{4})")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha de vencimiento debe tener formato MM/AA o MM/AAAA"
            );
        }

        if (request.getCvv() == null
                || !request.getCvv()
                .trim()
                .matches("\\d{3,4}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El CVV debe tener 3 o 4 dígitos"
            );
        }

        if (request.getMoneda() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La moneda de la tarjeta es obligatoria"
            );
        }
    }

    private void validarCheque(
            ChequeRequestDTO request
    ) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos del cheque son obligatorios"
            );
        }

        if (request.getIdentificacion() == null
                || request.getIdentificacion()
                .trim()
                .length() < 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La identificación es obligatoria"
            );
        }

        if (request.getNroCheque() == null
                || !request.getNroCheque()
                .trim()
                .matches("\\d+")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El número de cheque es obligatorio"
            );
        }

        if (request.getBeneficiario() == null
                || request.getBeneficiario()
                .trim()
                .length() < 3) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El beneficiario es obligatorio"
            );
        }

        if (request.getCuilCuit() == null
                || !request.getCuilCuit()
                .trim()
                .matches("\\d{11}")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El CUIL/CUIT debe tener 11 dígitos"
            );
        }

        if (request.getSaldo() == null
                || request.getSaldo() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El saldo del cheque debe ser mayor a cero"
            );
        }

        if (request.getMoneda() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La moneda del cheque es obligatoria"
            );
        }
    }

    private Float obtenerLimiteCreditoTarjeta(
            Moneda moneda
    ) {
        if (moneda.esMismaMoneda(Moneda.ARS)) {
            return LIMITE_TARJETA_ARS;
        }

        if (moneda.esMismaMoneda(Moneda.DOLARES)) {
            return LIMITE_TARJETA_DOLARES;
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Moneda no soportada para tarjeta"
        );
    }

    private TarjetaResponseDTO toTarjetaResponseDTO(
            TarjetaCredito tarjeta
    ) {
        return new TarjetaResponseDTO(
                tarjeta.getIdMedioPago(),
                enmascararNumero(tarjeta.getNumero()),
                tarjeta.getNombre(),
                tarjeta.getFechaVto(),
                tarjeta.getMoneda().name(),
                tarjeta.getLimiteCredito()
        );
    }

    private ChequeResponseDTO toChequeResponseDTO(
            Cheque cheque
    ) {
        return new ChequeResponseDTO(
                cheque.getIdMedioPago(),
                cheque.getIdentificacion(),
                cheque.getNroCheque(),
                cheque.getBeneficiario(),
                cheque.getCuilCuit(),
                cheque.getSaldo(),
                cheque.getMoneda().name()
        );
    }

    private String enmascararNumero(String numero) {
        if (numero == null || numero.length() < 4) {
            return "****";
        }

        return "**** "
                + numero.substring(numero.length() - 4);
    }
}