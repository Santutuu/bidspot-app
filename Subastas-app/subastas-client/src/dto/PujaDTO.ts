export type MonedaPuja = "PESOS" | "DOLARES" | "ARS" | string;

export interface EstadoPujaSubastaResponseDTO {
  idSubasta: number;
  idItemCatalogo: number;
  precioBase: number;
  mejorOferta: number;
  incrementoMinimo: number;
  incrementoMaximo: number;
  ofertaMinimaPermitida: number | null;
  ofertaMaximaPermitida: number | null;
  moneda: MonedaPuja;
  miMejorOferta: number | null;
  soyMejorPostor: boolean;
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
