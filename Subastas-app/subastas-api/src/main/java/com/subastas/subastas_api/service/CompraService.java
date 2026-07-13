package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.venta.*;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.FacturaRepository;
import com.subastas.subastas_api.repository.MedioDePagoRepository;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.repository.VentaConcretadaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class CompraService {

    private static final float COSTO_ENVIO_FIJO =
            999f;

    private final VentaConcretadaRepository
            ventaRepository;

    private final UsuarioRepository
            usuarioRepository;

    private final MedioDePagoRepository
            medioDePagoRepository;

    private final FacturaRepository
            facturaRepository;

    public CompraService(
            VentaConcretadaRepository ventaRepository,
            UsuarioRepository usuarioRepository,
            MedioDePagoRepository medioDePagoRepository,
            FacturaRepository facturaRepository
    ) {
        this.ventaRepository =
                ventaRepository;

        this.usuarioRepository =
                usuarioRepository;

        this.medioDePagoRepository =
                medioDePagoRepository;

        this.facturaRepository =
                facturaRepository;
    }

    @Transactional(readOnly = true)
    public List<VentaResumenResponseDTO>
    listarMisCompras(
            String mail
    ) {
        Cliente cliente =
                obtenerClientePorMail(mail);

        return ventaRepository
                .findByCompradorOrderByFechaVentaDesc(
                        cliente
                )
                .stream()
                .map(this::toResumenDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VentaDetalleResponseDTO
    obtenerDetalleCompra(
            String mail,
            Long idVenta
    ) {
        Cliente cliente =
                obtenerClientePorMail(mail);

        VentaConcretada venta =
                obtenerVentaDelCliente(
                        idVenta,
                        cliente
                );

        return toDetalleDTO(venta);
    }

    @Transactional
    public VentaDetalleResponseDTO
    configurarEntrega(
            String mail,
            Long idVenta,
            ConfigurarEntregaRequestDTO request
    ) {
        Cliente cliente =
                obtenerClientePorMail(mail);

        VentaConcretada venta =
                obtenerVentaDelCliente(
                        idVenta,
                        cliente
                );

        validarVentaPendientePago(venta);

        if (request == null
                || request.getTipoEntrega() == null
                || request.getTipoEntrega().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de entrega es obligatorio"
            );
        }

        TipoEntrega tipoEntrega =
                convertirTipoEntrega(
                        request.getTipoEntrega()
                );

        if (tipoEntrega
                == TipoEntrega.DOMICILIO) {

            String direccion =
                    request.getDireccionEntrega();

            if (direccion == null
                    || direccion.isBlank()) {

                direccion =
                        obtenerDireccionPerfil(
                                cliente
                        );
            }

            venta.configurarEntregaDomicilio(
                    direccion,
                    COSTO_ENVIO_FIJO
            );

        } else {

            venta.configurarRetiro(
                    venta.getUbicacionRetiro()
            );
        }

        return toDetalleDTO(
                ventaRepository.save(venta)
        );
    }

    @Transactional
    public VentaDetalleResponseDTO
    seleccionarMedioPago(
            String mail,
            Long idVenta,
            SeleccionarMedioPagoRequestDTO request
    ) {
        Cliente cliente =
                obtenerClientePorMail(mail);

        VentaConcretada venta =
                obtenerVentaDelCliente(
                        idVenta,
                        cliente
                );

        validarVentaPendientePago(venta);

        if (request == null
                || request.getIdMedioPago() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un medio de pago"
            );
        }

        MedioDePago medioPago =
                obtenerMedioPago(
                        request.getIdMedioPago(),
                        cliente
                );

        validarMoneda(
                venta,
                medioPago
        );

        venta.seleccionarMedioPago(
                medioPago
        );

        return toDetalleDTO(
                ventaRepository.save(venta)
        );
    }

    @Transactional
    public VentaDetalleResponseDTO
    confirmarCompra(
            String mail,
            Long idVenta,
            ConfirmarCompraRequestDTO request
    ) {
        Cliente cliente =
                obtenerClientePorMail(mail);

        VentaConcretada venta =
                obtenerVentaDelCliente(
                        idVenta,
                        cliente
                );

        validarVentaPendientePago(venta);

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de confirmación son obligatorios"
            );
        }

        if (venta.estaVencida()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La compra superó el plazo de 72 horas"
            );
        }

        if (request.getTipoEntrega() != null
                && !request.getTipoEntrega().isBlank()) {

            configurarEntrega(
                    mail,
                    idVenta,
                    new ConfigurarEntregaRequestDTOAdapter(
                            request
                    )
            );

            venta =
                    obtenerVentaDelCliente(
                            idVenta,
                            cliente
                    );
        }

        if (request.getIdMedioPago() != null) {

            seleccionarMedioPago(
                    mail,
                    idVenta,
                    new SeleccionarMedioPagoRequestDTOAdapter(
                            request
                    )
            );

            venta =
                    obtenerVentaDelCliente(
                            idVenta,
                            cliente
                    );
        }

        if (venta.getTipoEntrega() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe configurar la entrega antes de confirmar"
            );
        }

        MedioDePago medioPago =
                venta.getMedioPago();

        if (medioPago == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un medio de pago antes de confirmar"
            );
        }

        validarMoneda(
                venta,
                medioPago
        );

        cobrarMedioPago(
                medioPago,
                venta.getTotal()
        );

        venta.confirmarPago();

        Factura factura =
                crearFactura(venta);

        facturaRepository.save(factura);

        venta.asociarFactura(factura);

        VentaConcretada ventaGuardada =
                ventaRepository.save(venta);

        return toDetalleDTO(
                ventaGuardada
        );
    }

    @Transactional(readOnly = true)
    public VentaDetalleResponseDTO
    obtenerEstadoCompra(
            String mail,
            Long idVenta
    ) {
        Cliente cliente =
                obtenerClientePorMail(mail);

        return toDetalleDTO(
                obtenerVentaDelCliente(
                        idVenta,
                        cliente
                )
        );
    }

    @Transactional(readOnly = true)
    public FacturaResponseDTO obtenerFactura(
            String mail,
            Long idVenta
    ) {
        Cliente cliente =
                obtenerClientePorMail(mail);

        VentaConcretada venta =
                obtenerVentaDelCliente(
                        idVenta,
                        cliente
                );

        Factura factura =
                venta.getFactura();

        if (factura == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "La compra todavía no tiene una factura emitida"
            );
        }

        return toFacturaDTO(
                venta,
                factura
        );
    }

    private void cobrarMedioPago(
            MedioDePago medioPago,
            Float total
    ) {
        if (total == null
                || total <= 0f) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El total de la compra es inválido"
            );
        }

        if (medioPago
                instanceof TarjetaCredito tarjeta) {

            if (!tarjeta
                    .tieneLimiteSuficiente(total)) {

                throw new ResponseStatusException(
                        HttpStatus.PAYMENT_REQUIRED,
                        "La tarjeta no tiene límite suficiente para pagar la compra"
                );
            }

            tarjeta.consumirLimite(total);
            medioDePagoRepository.save(tarjeta);

            return;
        }

        if (medioPago
                instanceof Cheque cheque) {

            if (!cheque
                    .tieneSaldoSuficiente(total)) {

                throw new ResponseStatusException(
                        HttpStatus.PAYMENT_REQUIRED,
                        "El cheque no tiene saldo suficiente para pagar la compra"
                );
            }

            cheque.consumirSaldo(total);
            medioDePagoRepository.save(cheque);

            return;
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Tipo de medio de pago no soportado"
        );
    }

    private void validarMoneda(
            VentaConcretada venta,
            MedioDePago medioPago
    ) {
        Moneda monedaVenta =
                venta.getItemCatalogo()
                        .getCatalogo()
                        .getSubasta()
                        .getMoneda();

        if (monedaVenta == null
                || medioPago.getMoneda() == null
                || !monedaVenta.esMismaMoneda(
                medioPago.getMoneda()
        )) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El medio de pago no corresponde a la moneda de la compra"
            );
        }
    }

    private Factura crearFactura(
            VentaConcretada venta
    ) {
        if (venta.getFactura() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La compra ya tiene una factura emitida"
            );
        }

        Subasta subasta =
                venta.getItemCatalogo()
                        .getCatalogo()
                        .getSubasta();

        return new Factura(
                new Date(),
                valor(venta.getMontoPuja()),
                valor(venta.getComision()),
                valor(venta.getCostoEnvio()),
                valor(venta.getTotal()),
                subasta.getMoneda() != null
                        ? subasta.getMoneda().name()
                        : null,
                venta.getComprador(),
                venta.getItemCatalogo().getItem()
        );
    }

    private float valor(
            Float numero
    ) {
        return numero != null
                ? numero
                : 0f;
    }

    private MedioDePago obtenerMedioPago(
            Long idMedioPago,
            Cliente cliente
    ) {
        return medioDePagoRepository
                .findByIdMedioPagoAndCliente(
                        idMedioPago,
                        cliente
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe un medio de pago con ese id para este cliente"
                        )
                );
    }

    private TipoEntrega convertirTipoEntrega(
            String valor
    ) {
        try {
            return TipoEntrega.valueOf(
                    valor.trim()
                            .toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de entrega inválido"
            );
        }
    }

    private String obtenerDireccionPerfil(
            Cliente cliente
    ) {
        if (cliente.getPersona() == null
                || cliente.getPersona()
                .getDomicilio() == null) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El cliente no tiene domicilio registrado"
            );
        }

        Domicilio domicilio =
                cliente.getPersona()
                        .getDomicilio();

        StringBuilder direccion =
                new StringBuilder();

        agregarParte(
                direccion,
                domicilio.getDireccion()
        );

        agregarParte(
                direccion,
                domicilio.getCiudad()
        );

        agregarParte(
                direccion,
                domicilio.getProvincia()
        );

        agregarParte(
                direccion,
                domicilio.getCp()
        );

        agregarParte(
                direccion,
                domicilio.getPais()
        );

        if (direccion.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El domicilio del cliente está vacío"
            );
        }

        return direccion.toString();
    }

    private void agregarParte(
            StringBuilder builder,
            String valor
    ) {
        if (valor == null
                || valor.isBlank()) {
            return;
        }

        if (!builder.isEmpty()) {
            builder.append(", ");
        }

        builder.append(valor.trim());
    }

    private Usuario obtenerUsuario(
            String mail
    ) {
        if (mail == null
                || mail.isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario no autenticado"
            );
        }

        return usuarioRepository
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
    }

    private Cliente obtenerClientePorMail(
            String mail
    ) {
        Usuario usuario =
                obtenerUsuario(mail);

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

    private VentaConcretada obtenerVentaDelCliente(
            Long idVenta,
            Cliente cliente
    ) {
        return ventaRepository
                .findByIdVentaAndComprador(
                        idVenta,
                        cliente
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No existe una compra con ese id para este cliente"
                        )
                );
    }

    private void validarVentaPendientePago(
            VentaConcretada venta
    ) {
        if (venta.getEstado()
                != EstadoVenta.PENDIENTE_PAGO) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La compra ya no está pendiente de pago"
            );
        }

        if (venta.estaVencida()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La compra superó el plazo de pago"
            );
        }
    }

    private VentaResumenResponseDTO
    toResumenDTO(
            VentaConcretada venta
    ) {
        Item item =
                venta.getItemCatalogo()
                        .getItem();

        Subasta subasta =
                venta.getItemCatalogo()
                        .getCatalogo()
                        .getSubasta();

        return new VentaResumenResponseDTO(
                venta.getIdVenta(),
                item.getTitulo(),
                item.getPrimeraImagen(),
                venta.getTotal(),
                subasta.getMoneda() != null
                        ? subasta.getMoneda().name()
                        : null,
                venta.getEstado().name()
        );
    }

    private VentaDetalleResponseDTO
    toDetalleDTO(
            VentaConcretada venta
    ) {
        ItemCatalogo itemCatalogo =
                venta.getItemCatalogo();

        Item item =
                itemCatalogo.getItem();

        Subasta subasta =
                itemCatalogo
                        .getCatalogo()
                        .getSubasta();

        return new VentaDetalleResponseDTO(
                venta.getIdVenta(),
                itemCatalogo
                        .getIdItemCatalogo(),
                subasta.getIdSubasta(),
                item.getTitulo(),
                item.getPrimeraImagen(),
                venta.getEstado().name(),
                venta.getMontoPuja(),
                venta.getComision(),
                venta.getCostoEnvio(),
                venta.getTotal(),
                subasta.getMoneda() != null
                        ? subasta.getMoneda().name()
                        : null,
                venta.getTipoEntrega() != null
                        ? venta.getTipoEntrega().name()
                        : null,
                venta.getDireccionEntrega(),
                venta.getUbicacionRetiro(),
                venta.getMedioPago() != null
                        ? venta.getMedioPago()
                        .getIdMedioPago()
                        : null,
                venta.getFactura() != null
                        ? venta.getFactura()
                        .getIdFactura()
                        : null,
                venta.getFechaVenta(),
                venta.getFechaLimitePago(),
                venta.getFechaPagoConfirmado(),
                venta.getFechaIncumplimiento()
        );
    }

    private FacturaResponseDTO toFacturaDTO(
            VentaConcretada venta,
            Factura factura
    ) {
        return new FacturaResponseDTO(
                factura.getIdFactura(),
                factura.getFechaEmision(),
                venta.getIdVenta(),
                venta.getItemCatalogo()
                        .getIdItemCatalogo(),
                venta.getItemCatalogo()
                        .getItem()
                        .getTitulo(),
                factura.getMontoPuja(),
                factura.getComision(),
                factura.getCostoEnvio(),
                factura.getTotal(),
                factura.getMoneda()
        );
    }

    private static class
    ConfigurarEntregaRequestDTOAdapter
            extends ConfigurarEntregaRequestDTO {

        private final ConfirmarCompraRequestDTO
                request;

        private ConfigurarEntregaRequestDTOAdapter(
                ConfirmarCompraRequestDTO request
        ) {
            this.request = request;
        }

        @Override
        public String getTipoEntrega() {
            return request.getTipoEntrega();
        }

        @Override
        public String getDireccionEntrega() {
            return request.getDireccionEntrega();
        }
    }

    private static class
    SeleccionarMedioPagoRequestDTOAdapter
            extends SeleccionarMedioPagoRequestDTO {

        private final ConfirmarCompraRequestDTO
                request;

        private SeleccionarMedioPagoRequestDTOAdapter(
                ConfirmarCompraRequestDTO request
        ) {
            this.request = request;
        }

        @Override
        public Long getIdMedioPago() {
            return request.getIdMedioPago();
        }
    }
}