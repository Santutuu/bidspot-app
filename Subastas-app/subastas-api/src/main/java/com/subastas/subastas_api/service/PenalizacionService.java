package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.penalizacion.PagarPenalizacionRequestDTO;
import com.subastas.subastas_api.DTO.penalizacion.PenalizacionResponseDTO;
import com.subastas.subastas_api.model.Cheque;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.EstadoPenalizacion;
import com.subastas.subastas_api.model.MedioDePago;
import com.subastas.subastas_api.model.Penalizacion;
import com.subastas.subastas_api.model.TarjetaCredito;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.repository.MedioDePagoRepository;
import com.subastas.subastas_api.repository.PenalizacionRepository;
import com.subastas.subastas_api.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PenalizacionService {

    private final PenalizacionRepository
            penalizacionRepository;

    private final MedioDePagoRepository
            medioDePagoRepository;

    private final UsuarioRepository
            usuarioRepository;

    public PenalizacionService(
            PenalizacionRepository penalizacionRepository,
            MedioDePagoRepository medioDePagoRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.penalizacionRepository =
                penalizacionRepository;

        this.medioDePagoRepository =
                medioDePagoRepository;

        this.usuarioRepository =
                usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<PenalizacionResponseDTO>
    listarMisPenalizaciones(
            String mail
    ) {
        Cliente cliente =
                obtenerClientePorMail(mail);

        return penalizacionRepository
                .findByClienteOrderByFechaGeneracionDesc(
                        cliente
                )
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PenalizacionResponseDTO
    obtenerPenalizacion(
            String mail,
            Long idPenalizacion
    ) {
        Cliente cliente =
                obtenerClientePorMail(mail);

        Penalizacion penalizacion =
                obtenerPenalizacionDelCliente(
                        idPenalizacion,
                        cliente
                );

        return toDTO(
                penalizacion
        );
    }

    @Transactional
    public PenalizacionResponseDTO
    pagarPenalizacion(
            String mail,
            Long idPenalizacion,
            PagarPenalizacionRequestDTO request
    ) {
        Cliente cliente =
                obtenerClientePorMail(mail);

        if (request == null
                || request.getIdMedioPago() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un medio de pago"
            );
        }

        Penalizacion penalizacion =
                obtenerPenalizacionDelCliente(
                        idPenalizacion,
                        cliente
                );

        if (!penalizacion.estaPendiente()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La penalización ya no está pendiente"
            );
        }

        MedioDePago medioPago =
                medioDePagoRepository
                        .findByIdMedioPagoAndCliente(
                                request.getIdMedioPago(),
                                cliente
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "No existe el medio de pago indicado para este cliente"
                                )
                        );

        validarMoneda(
                penalizacion,
                medioPago
        );

        cobrar(
                medioPago,
                penalizacion.getImporte()
        );

        penalizacion.marcarPagada();

        Penalizacion penalizacionGuardada =
                penalizacionRepository.save(
                        penalizacion
                );

        /*
         * La consulta se ejecuta después de marcar esta
         * penalización como PAGADA.
         *
         * Si no quedan otras pendientes, se rehabilita
         * comercialmente al cliente.
         */
        boolean conservaPenalizacionesPendientes =
                penalizacionRepository
                        .existsByClienteAndEstado(
                                cliente,
                                EstadoPenalizacion.PENDIENTE
                        );

        if (!conservaPenalizacionesPendientes) {
            cliente.habilitarParaPujar();
        }

        return toDTO(
                penalizacionGuardada
        );
    }

    private Penalizacion obtenerPenalizacionDelCliente(
            Long idPenalizacion,
            Cliente cliente
    ) {
        if (idPenalizacion == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El id de la penalización es obligatorio"
            );
        }

        return penalizacionRepository
                .findByIdPenalizacionAndCliente(
                        idPenalizacion,
                        cliente
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe una penalización con ese id para este cliente"
                        )
                );
    }

    private void validarMoneda(
            Penalizacion penalizacion,
            MedioDePago medioPago
    ) {
        if (penalizacion.getMoneda() == null
                || medioPago.getMoneda() == null
                || !penalizacion
                .getMoneda()
                .esMismaMoneda(
                        medioPago.getMoneda()
                )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La moneda del medio de pago no coincide con la penalización"
            );
        }
    }

    /**
     * Penalizacion trabaja con BigDecimal.
     *
     * Los medios legacy todavía trabajan con Float,
     * por eso la conversión se hace únicamente aquí.
     */
    private void cobrar(
            MedioDePago medioPago,
            BigDecimal importe
    ) {
        if (importe == null
                || importe.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El importe de la penalización es inválido"
            );
        }

        Float importeFloat =
                importe.floatValue();

        if (medioPago
                instanceof TarjetaCredito tarjeta) {

            if (!tarjeta
                    .tieneLimiteSuficiente(
                            importeFloat
                    )) {

                throw new ResponseStatusException(
                        HttpStatus.PAYMENT_REQUIRED,
                        "La tarjeta no tiene límite suficiente para pagar la penalización"
                );
            }

            tarjeta.consumirLimite(
                    importeFloat
            );

            medioDePagoRepository.save(
                    tarjeta
            );

            return;
        }

        if (medioPago
                instanceof Cheque cheque) {

            if (!cheque
                    .tieneSaldoSuficiente(
                            importeFloat
                    )) {

                throw new ResponseStatusException(
                        HttpStatus.PAYMENT_REQUIRED,
                        "El cheque no tiene saldo suficiente para pagar la penalización"
                );
            }

            cheque.consumirSaldo(
                    importeFloat
            );

            medioDePagoRepository.save(
                    cheque
            );

            return;
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "El tipo de medio de pago no está soportado"
        );
    }

    private Cliente obtenerClientePorMail(
            String mail
    ) {
        if (mail == null
                || mail.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        Usuario usuario =
                usuarioRepository
                        .findByMail(
                                mail.trim()
                                        .toLowerCase()
                        )
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.UNAUTHORIZED,
                                        "Usuario no autenticado"
                                )
                        );

        Cliente cliente =
                usuario.getCliente();

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

    private PenalizacionResponseDTO toDTO(
            Penalizacion penalizacion
    ) {
        return new PenalizacionResponseDTO(
                penalizacion.getIdPenalizacion(),

                penalizacion.getVenta() != null
                        ? penalizacion
                        .getVenta()
                        .getIdVenta()
                        : null,

                penalizacion.getImporte(),

                penalizacion.getMoneda() != null
                        ? penalizacion
                        .getMoneda()
                        .name()
                        : null,

                penalizacion.getTipo() != null
                        ? penalizacion
                        .getTipo()
                        .name()
                        : null,

                penalizacion.getEstado() != null
                        ? penalizacion
                        .getEstado()
                        .name()
                        : null,

                penalizacion.getFechaGeneracion(),

                penalizacion.getFechaPago()
        );
    }
}