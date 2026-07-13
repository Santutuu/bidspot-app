package com.subastas.subastas_api.service;

import com.subastas.subastas_api.model.Cheque;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.Moneda;
import com.subastas.subastas_api.model.TarjetaCredito;
import com.subastas.subastas_api.repository.ChequeRepository;
import com.subastas.subastas_api.repository.TarjetaCreditoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DisponibilidadPagoService {

    private final TarjetaCreditoRepository tarjetaCreditoRepository;
    private final ChequeRepository chequeRepository;

    public DisponibilidadPagoService(
            TarjetaCreditoRepository tarjetaCreditoRepository,
            ChequeRepository chequeRepository
    ) {
        this.tarjetaCreditoRepository =
                tarjetaCreditoRepository;

        this.chequeRepository =
                chequeRepository;
    }

    @Transactional(readOnly = true)
    public boolean tieneRespaldoSuficiente(
            Cliente cliente,
            Moneda moneda,
            Float monto
    ) {
        validarParametros(
                cliente,
                moneda,
                monto
        );

        return existeTarjetaConLimiteSuficiente(
                cliente,
                moneda,
                monto
        ) || existeChequeConSaldoSuficiente(
                cliente,
                moneda,
                monto
        );
    }

    @Transactional(readOnly = true)
    public boolean existeTarjetaConLimiteSuficiente(
            Cliente cliente,
            Moneda moneda,
            Float monto
    ) {
        validarParametros(
                cliente,
                moneda,
                monto
        );

        List<TarjetaCredito> tarjetas =
                tarjetaCreditoRepository
                        .findByCliente(cliente);

        return tarjetas
                .stream()
                .anyMatch(tarjeta ->
                        tieneMonedaCompatible(
                                tarjeta.getMoneda(),
                                moneda
                        )
                                &&
                                tieneMontoSuficiente(
                                        tarjeta.getLimiteCredito(),
                                        monto
                                )
                );
    }

    @Transactional(readOnly = true)
    public boolean existeChequeConSaldoSuficiente(
            Cliente cliente,
            Moneda moneda,
            Float monto
    ) {
        validarParametros(
                cliente,
                moneda,
                monto
        );

        List<Cheque> cheques =
                chequeRepository
                        .findByCliente(cliente);

        return cheques
                .stream()
                .anyMatch(cheque ->
                        tieneMonedaCompatible(
                                cheque.getMoneda(),
                                moneda
                        )
                                &&
                                tieneMontoSuficiente(
                                        cheque.getSaldo(),
                                        monto
                                )
                );
    }

    private boolean tieneMonedaCompatible(
            Moneda monedaMedioPago,
            Moneda monedaSubasta
    ) {
        return monedaMedioPago != null
                && monedaSubasta != null
                && monedaMedioPago
                .esMismaMoneda(monedaSubasta);
    }

    private boolean tieneMontoSuficiente(
            Float disponible,
            Float monto
    ) {
        return disponible != null
                && monto != null
                && disponible >= monto;
    }

    private void validarParametros(
            Cliente cliente,
            Moneda moneda,
            Float monto
    ) {
        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio"
            );
        }

        if (moneda == null) {
            throw new IllegalArgumentException(
                    "La moneda es obligatoria"
            );
        }

        if (monto == null || monto <= 0f) {
            throw new IllegalArgumentException(
                    "El monto debe ser positivo"
            );
        }
    }
}