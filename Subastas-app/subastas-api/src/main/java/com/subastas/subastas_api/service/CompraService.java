package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.venta.ConfigurarEntregaRequestDTO;
import com.subastas.subastas_api.DTO.venta.ConfirmarCompraRequestDTO;
import com.subastas.subastas_api.DTO.venta.SeleccionarMedioPagoRequestDTO;
import com.subastas.subastas_api.DTO.venta.VentaDetalleResponseDTO;
import com.subastas.subastas_api.DTO.venta.VentaResumenResponseDTO;
import com.subastas.subastas_api.model.Cliente;
import com.subastas.subastas_api.model.EstadoVenta;
import com.subastas.subastas_api.model.Item;
import com.subastas.subastas_api.model.ItemCatalogo;
import com.subastas.subastas_api.model.MedioDePago;
import com.subastas.subastas_api.model.Subasta;
import com.subastas.subastas_api.model.TipoEntrega;
import com.subastas.subastas_api.model.Usuario;
import com.subastas.subastas_api.model.VentaConcretada;
import com.subastas.subastas_api.repository.MedioDePagoRepository;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.repository.VentaConcretadaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CompraService {

    private static final float COSTO_ENVIO_FIJO = 999f;

    private final VentaConcretadaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MedioDePagoRepository medioDePagoRepository;

    public CompraService(
            VentaConcretadaRepository ventaRepository,
            UsuarioRepository usuarioRepository,
            MedioDePagoRepository medioDePagoRepository
    ) {
        this.ventaRepository = ventaRepository;
        this.usuarioRepository = usuarioRepository;
        this.medioDePagoRepository = medioDePagoRepository;
    }

    @Transactional(readOnly = true)
    public List<VentaResumenResponseDTO> listarMisCompras(
            String mail
    ) {
        Cliente cliente = obtenerClientePorMail(mail);

        return ventaRepository
                .findByCompradorOrderByFechaVentaDesc(cliente)
                .stream()
                .map(this::toResumenDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VentaDetalleResponseDTO obtenerDetalleCompra(
            String mail,
            Long idVenta
    ) {
        Cliente cliente = obtenerClientePorMail(mail);

        VentaConcretada venta =
                obtenerVentaDelCliente(idVenta, cliente);

        return toDetalleDTO(venta);
    }

    @Transactional
    public VentaDetalleResponseDTO configurarEntrega(
            String mail,
            Long idVenta,
            ConfigurarEntregaRequestDTO request
    ) {
        Cliente cliente = obtenerClientePorMail(mail);

        VentaConcretada venta =
                obtenerVentaDelCliente(idVenta, cliente);

        validarVentaPendientePago(venta);

        if (request == null
                || request.getTipoEntrega() == null
                || request.getTipoEntrega().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de entrega es obligatorio"
            );
        }

        TipoEntrega tipoEntrega;

        try {
            tipoEntrega = TipoEntrega.valueOf(
                    request.getTipoEntrega()
                            .trim()
                            .toUpperCase()
            );
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de entrega inválido"
            );
        }

        if (tipoEntrega == TipoEntrega.DOMICILIO) {
            if (request.getDireccionEntrega() == null
                    || request.getDireccionEntrega().isBlank()) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La dirección de entrega es obligatoria"
                );
            }

            venta.configurarEntregaDomicilio(
                    request.getDireccionEntrega().trim(),
                    COSTO_ENVIO_FIJO
            );
        } else {
            venta.configurarRetiro(
                    venta.getUbicacionRetiro()
            );
        }

        VentaConcretada ventaGuardada =
                ventaRepository.save(venta);

        return toDetalleDTO(ventaGuardada);
    }

    @Transactional
    public VentaDetalleResponseDTO seleccionarMedioPago(
            String mail,
            Long idVenta,
            SeleccionarMedioPagoRequestDTO request
    ) {
        Cliente cliente = obtenerClientePorMail(mail);

        VentaConcretada venta =
                obtenerVentaDelCliente(idVenta, cliente);

        validarVentaPendientePago(venta);

        if (request == null
                || request.getIdMedioPago() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un medio de pago"
            );
        }

        MedioDePago medioPago = medioDePagoRepository
                .findByIdMedioPagoAndCliente(
                        request.getIdMedioPago(),
                        cliente
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un medio de pago con ese id para este cliente"
                ));

        venta.seleccionarMedioPago(medioPago);

        VentaConcretada ventaGuardada =
                ventaRepository.save(venta);

        return toDetalleDTO(ventaGuardada);
    }

    @Transactional
    public VentaDetalleResponseDTO confirmarCompra(
            String mail,
            Long idVenta,
            ConfirmarCompraRequestDTO request
    ) {
        Cliente cliente = obtenerClientePorMail(mail);

        VentaConcretada venta =
                obtenerVentaDelCliente(idVenta, cliente);

        validarVentaPendientePago(venta);

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de confirmación son obligatorios"
            );
        }

        if (request.getTipoEntrega() != null
                && !request.getTipoEntrega().isBlank()) {

            configurarEntrega(
                    mail,
                    idVenta,
                    new ConfigurarEntregaRequestDTOAdapter(request)
            );

            venta = obtenerVentaDelCliente(
                    idVenta,
                    cliente
            );
        }

        if (request.getIdMedioPago() != null) {
            seleccionarMedioPago(
                    mail,
                    idVenta,
                    new SeleccionarMedioPagoRequestDTOAdapter(request)
            );

            venta = obtenerVentaDelCliente(
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

        if (venta.getMedioPago() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un medio de pago antes de confirmar"
            );
        }

        venta.confirmarPago();

        VentaConcretada ventaGuardada =
                ventaRepository.save(venta);

        return toDetalleDTO(ventaGuardada);
    }

    @Transactional(readOnly = true)
    public VentaDetalleResponseDTO obtenerEstadoCompra(
            String mail,
            Long idVenta
    ) {
        Cliente cliente = obtenerClientePorMail(mail);

        VentaConcretada venta =
                obtenerVentaDelCliente(idVenta, cliente);

        return toDetalleDTO(venta);
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
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no autenticado"
                ));
    }

    private Cliente obtenerClientePorMail(String mail) {
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

    private VentaConcretada obtenerVentaDelCliente(
            Long idVenta,
            Cliente cliente
    ) {
        return ventaRepository
                .findByIdVentaAndComprador(
                        idVenta,
                        cliente
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una compra con ese id para este cliente"
                ));
    }

    private void validarVentaPendientePago(
            VentaConcretada venta
    ) {
        if (venta.getEstado() != EstadoVenta.PENDIENTE_PAGO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La compra ya no está pendiente de pago"
            );
        }
    }

    private VentaResumenResponseDTO toResumenDTO(
            VentaConcretada venta
    ) {
        Item item =
                venta.getItemCatalogo().getItem();

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

    private VentaDetalleResponseDTO toDetalleDTO(
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
                itemCatalogo.getIdItemCatalogo(),
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
                venta.getFechaVenta(),
                venta.getFechaPagoConfirmado()
        );
    }

    private static class ConfigurarEntregaRequestDTOAdapter
            extends ConfigurarEntregaRequestDTO {

        private final ConfirmarCompraRequestDTO request;

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

    private static class SeleccionarMedioPagoRequestDTOAdapter
            extends SeleccionarMedioPagoRequestDTO {

        private final ConfirmarCompraRequestDTO request;

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