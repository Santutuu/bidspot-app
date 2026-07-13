import api from "@/src/api/axios";
import {
  ConfigurarEntregaRequest,
  ConfirmarCompraRequest,
  FacturaResponse,
  SeleccionarMedioPagoRequest,
  VentaDetalleResponse,
  VentaResumenResponse,
} from "@/src/dto/CompraDTO";

export async function listarMisCompras(): Promise<VentaResumenResponse[]> {
  const response = await api.get<VentaResumenResponse[]>("/me/compras");
  return response.data;
}

export async function obtenerDetalleCompra(
  idVenta: string | number,
): Promise<VentaDetalleResponse> {
  const response = await api.get<VentaDetalleResponse>(
    `/me/compras/${idVenta}`,
  );
  return response.data;
}

export async function configurarEntrega(
  idVenta: string | number,
  request: ConfigurarEntregaRequest,
): Promise<VentaDetalleResponse> {
  const response = await api.post<VentaDetalleResponse>(
    `/me/compras/${idVenta}/entrega`,
    request,
  );
  return response.data;
}

export async function seleccionarMedioPago(
  idVenta: string | number,
  request: SeleccionarMedioPagoRequest,
): Promise<VentaDetalleResponse> {
  const response = await api.post<VentaDetalleResponse>(
    `/me/compras/${idVenta}/medio-pago`,
    request,
  );
  return response.data;
}

export async function confirmarCompra(
  idVenta: string | number,
  request: ConfirmarCompraRequest,
): Promise<VentaDetalleResponse> {
  const response = await api.post<VentaDetalleResponse>(
    `/me/compras/${idVenta}/confirmar`,
    request,
  );
  return response.data;
}

export async function obtenerEstadoCompra(
  idVenta: string | number,
): Promise<VentaDetalleResponse> {
  const response = await api.get<VentaDetalleResponse>(
    `/me/compras/${idVenta}/estado`,
  );
  return response.data;
}

export async function obtenerFacturaCompra(
  idVenta: string | number,
): Promise<FacturaResponse> {
  const response = await api.get<FacturaResponse>(
    `/me/compras/${idVenta}/factura`,
  );
  return response.data;
}
