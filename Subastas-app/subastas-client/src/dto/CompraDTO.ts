export type EstadoVenta =
  | "PENDIENTE_PAGO"
  | "PAGO_CONFIRMADO"
  | "PREPARANDO_ENVIO"
  | "ENVIADO"
  | "EN_CAMINO"
  | "ENTREGADO"
  | "MULTADA"
  | "INCUMPLIDA"
  | "CANCELADA";

export type TipoEntrega = "DOMICILIO" | "RETIRO";

export interface VentaResumenResponse {
  idVenta: number;
  tituloItem: string;
  imagenUrl: string | null;
  total: number;
  moneda: "DOLARES" | "ARS" | "PESOS" | string;
  estado: EstadoVenta;
}

export interface VentaDetalleResponse extends VentaResumenResponse {
  idItemCatalogo: number;
  idSubasta: number;
  montoPuja: number;
  comision: number;
  costoEnvio: number;
  tipoEntrega: TipoEntrega | null;
  direccionEntrega: string | null;
  ubicacionRetiro: string | null;
  idMedioPago: number | null;
  fechaVenta: string;
  fechaPagoConfirmado: string | null;
}

export interface ConfigurarEntregaRequest {
  tipoEntrega: TipoEntrega;
  direccionEntrega?: string | null;
}

export interface SeleccionarMedioPagoRequest {
  idMedioPago: number;
}

export interface ConfirmarCompraRequest {
  idMedioPago: number;
  tipoEntrega: TipoEntrega;
  direccionEntrega: string | null;
}

export interface CompraEstadoResponse {
  idVenta?: number;
  estado: EstadoVenta;
  fechaPagoConfirmado?: string | null;
  entregaEstimada?: string | null;
}
