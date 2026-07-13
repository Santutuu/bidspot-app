export type Moneda = "ARS" | "PESOS" | "DOLARES" | string;

export type EstadoVenta =
  | "PENDIENTE_PAGO"
  | "PAGO_CONFIRMADO"
  | "PREPARANDO_ENVIO"
  | "ENVIADO"
  | "EN_CAMINO"
  | "ENTREGADO"
  | "PREPARANDO_RETIRO"
  | "LISTO_PARA_RETIRAR"
  | "RETIRADO"
  | "INCUMPLIDA"
  | "CANCELADA";

export type TipoEntrega = "DOMICILIO" | "RETIRO";

export interface VentaResumenResponse {
  idVenta: number;
  tituloItem: string;
  imagenUrl: string | null;
  total: number;
  moneda: Moneda;
  estado: EstadoVenta;
}

export interface VentaDetalleResponse {
  idVenta: number;
  idItemCatalogo: number;
  idSubasta: number;
  tituloItem: string;
  imagenUrl: string | null;
  estado: EstadoVenta;
  montoPuja: number;
  comision: number;
  costoEnvio: number;
  total: number;
  moneda: Moneda;
  tipoEntrega: TipoEntrega | null;
  direccionEntrega: string | null;
  ubicacionRetiro: string | null;
  idMedioPago: number | null;
  idFactura: number | null;
  fechaVenta: string;
  fechaLimitePago: string | null;
  fechaPagoConfirmado: string | null;
  fechaIncumplimiento: string | null;
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

export interface FacturaResponse {
  idFactura: number;
  fechaEmision: string;
  idVenta: number;
  idItemCatalogo: number;
  tituloItem: string;
  montoPuja: number;
  comision: number;
  costoEnvio: number;
  total: number;
  moneda: Moneda;
}

export type EstadoPenalizacion = "PENDIENTE" | "PAGADA" | "CANCELADA";

export type TipoPenalizacion = "FALTA_FONDOS";

export interface PenalizacionResponse {
  idPenalizacion: number;
  idVenta: number | null;
  importe: number;
  moneda: Moneda;
  tipo: TipoPenalizacion;
  estado: EstadoPenalizacion;
  fechaGeneracion: string;
  fechaPago: string | null;
}

export interface PagarPenalizacionRequest {
  idMedioPago: number;
}

export interface UltimaAdjudicacionResponse {
  tieneAdjudicacion: boolean;
  idVenta: number | null;
  idItemCatalogo: number | null;
  tituloItem: string | null;
  imagenUrl: string | null;
  montoPuja: number | null;
  comision: number | null;
  costoEnvio: number | null;
  total: number | null;
  moneda: Moneda | null;
  estado: EstadoVenta | null;
  fechaVenta: string | null;
  fechaLimitePago: string | null;
}
