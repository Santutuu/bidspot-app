package com.subastas.subastas_api.service;

import com.subastas.subastas_api.DTO.venta.*;
import com.subastas.subastas_api.model.*;
import com.subastas.subastas_api.repository.MedioDePagoRepository;
import com.subastas.subastas_api.repository.UsuarioRepository;
import com.subastas.subastas_api.repository.VentaConcretadaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CompraService {

    private static final float COSTO_ENVIO_FIJO = 999f;

    private final VentaConcretadaRepository ventaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MedioDePagoRepository medioDePagoRepository;

    public CompraService(VentaConcretadaRepository ventaRepository,
                         UsuarioRepository usuarioRepository,
                         MedioDePagoRepository medioDePagoRepository) {
        this.ventaRepository = ventaRepository;
        this.usuarioRepository = usuarioRepository;
        this.medioDePagoRepository = medioDePagoRepository;
    }

    public List<VentaResumenResponseDTO> listarMisCompras(String mail) {
        Usuario usuario = obtenerUsuario(mail);

        return ventaRepository.findByCompradorOrderByFechaVentaDesc(usuario)
                .stream()
                .map(this::toResumenDTO)
                .toList();
    }

    public VentaDetalleResponseDTO obtenerDetalleCompra(String mail, Long idVenta) {
        Usuario usuario = obtenerUsuario(mail);
        VentaConcretada venta = obtenerVentaDelUsuario(idVenta, usuario);

        return toDetalleDTO(venta);
    }

    public VentaDetalleResponseDTO configurarEntrega(String mail,
                                                     Long idVenta,
                                                     ConfigurarEntregaRequestDTO request) {
        Usuario usuario = obtenerUsuario(mail);
        VentaConcretada venta = obtenerVentaDelUsuario(idVenta, usuario);

        validarVentaPendientePago(venta);

        if (request.getTipoEntrega() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tipo de entrega es obligatorio"
            );
        }

        TipoEntrega tipoEntrega;

        try {
            tipoEntrega = TipoEntrega.valueOf(request.getTipoEntrega());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tipo de entrega inválido"
            );
        }

        if (tipoEntrega == TipoEntrega.DOMICILIO) {
            if (request.getDireccionEntrega() == null || request.getDireccionEntrega().isBlank()) {
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
            venta.configurarRetiro(venta.getUbicacionRetiro());
        }

        return toDetalleDTO(ventaRepository.save(venta));
    }

    public VentaDetalleResponseDTO seleccionarMedioPago(String mail,
                                                        Long idVenta,
                                                        SeleccionarMedioPagoRequestDTO request) {
        Usuario usuario = obtenerUsuario(mail);
        VentaConcretada venta = obtenerVentaDelUsuario(idVenta, usuario);

        validarVentaPendientePago(venta);

        if (request.getIdMedioPago() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar un medio de pago"
            );
        }

        MedioDePago medioPago = medioDePagoRepository
                .findByIdMedioPagoAndUsuario(request.getIdMedioPago(), usuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe un medio de pago con ese id"
                ));

        venta.seleccionarMedioPago(medioPago);

        return toDetalleDTO(ventaRepository.save(venta));
    }

    public VentaDetalleResponseDTO confirmarCompra(String mail,
                                                   Long idVenta,
                                                   ConfirmarCompraRequestDTO request) {
        Usuario usuario = obtenerUsuario(mail);
        VentaConcretada venta = obtenerVentaDelUsuario(idVenta, usuario);

        validarVentaPendientePago(venta);

        if (request.getTipoEntrega() != null) {
            configurarEntrega(mail, idVenta, new ConfigurarEntregaRequestDTOAdapter(request));
            venta = obtenerVentaDelUsuario(idVenta, usuario);
        }

        if (request.getIdMedioPago() != null) {
            seleccionarMedioPago(mail, idVenta, new SeleccionarMedioPagoRequestDTOAdapter(request));
            venta = obtenerVentaDelUsuario(idVenta, usuario);
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

        return toDetalleDTO(ventaRepository.save(venta));
    }

    public VentaDetalleResponseDTO obtenerEstadoCompra(String mail, Long idVenta) {
        return obtenerDetalleCompra(mail, idVenta);
    }

    private Usuario obtenerUsuario(String mail) {
        return usuarioRepository.findByMail(mail)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario no autenticado"
                ));
    }

    private VentaConcretada obtenerVentaDelUsuario(Long idVenta, Usuario usuario) {
        return ventaRepository.findByIdVentaAndComprador(idVenta, usuario)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe una compra con ese id para este usuario"
                ));
    }

    private void validarVentaPendientePago(VentaConcretada venta) {
        if (venta.getEstado() != EstadoVenta.PENDIENTE_PAGO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "La compra ya no está pendiente de pago"
            );
        }
    }

    private VentaResumenResponseDTO toResumenDTO(VentaConcretada venta) {
        Item item = venta.getItemCatalogo().getItem();
        Subasta subasta = venta.getItemCatalogo().getCatalogo().getSubasta();

        return new VentaResumenResponseDTO(
                venta.getIdVenta(),
                item.getTitulo(),
                item.getPrimeraImagen(),
                venta.getTotal(),
                subasta.getMoneda() != null ? subasta.getMoneda().name() : null,
                venta.getEstado().name()
        );
    }

    private VentaDetalleResponseDTO toDetalleDTO(VentaConcretada venta) {
        ItemCatalogo itemCatalogo = venta.getItemCatalogo();
        Item item = itemCatalogo.getItem();
        Subasta subasta = itemCatalogo.getCatalogo().getSubasta();

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
                subasta.getMoneda() != null ? subasta.getMoneda().name() : null,
                venta.getTipoEntrega() != null ? venta.getTipoEntrega().name() : null,
                venta.getDireccionEntrega(),
                venta.getUbicacionRetiro(),
                venta.getMedioPago() != null ? venta.getMedioPago().getIdMedioPago() : null,
                venta.getFechaVenta(),
                venta.getFechaPagoConfirmado()
        );
    }

    private static class ConfigurarEntregaRequestDTOAdapter extends ConfigurarEntregaRequestDTO {
        private final ConfirmarCompraRequestDTO request;

        public ConfigurarEntregaRequestDTOAdapter(ConfirmarCompraRequestDTO request) {
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

    private static class SeleccionarMedioPagoRequestDTOAdapter extends SeleccionarMedioPagoRequestDTO {
        private final ConfirmarCompraRequestDTO request;

        public SeleccionarMedioPagoRequestDTOAdapter(ConfirmarCompraRequestDTO request) {
            this.request = request;
        }

        @Override
        public Long getIdMedioPago() {
            return request.getIdMedioPago();
        }
    }
}