export type MonedaPuja = "PESOS" | "DOLARES" | "ARS" | string;

export interface EstadoPujaSubastaResponseDTO {
  idSubasta: number;
  idItemCatalogo: number;
  precioBase: number;
  mejorOferta: number;
  incrementoMinimo: number;
  incrementoMaximo: number;
  ofertaMinimaPermitida: number;
  ofertaMaximaPermitida: number | null;
  moneda: MonedaPuja;
}

export interface PujaRequestDTO {
  monto: number;
}

export interface PujaResponseDTO {
  idPuja: number;
  idSubasta: number;
  idItemCatalogo: number;
  monto: number;
  moneda: MonedaPuja;
  estado: "REGISTRADA" | "SUPERADA" | "GANADORA" | "RECHAZADA" | string;
  fechaHora: string;
  mejorOferta: boolean;
}
