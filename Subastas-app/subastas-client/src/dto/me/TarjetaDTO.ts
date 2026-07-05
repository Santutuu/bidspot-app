import { MonedaMedioPago } from "@/src/dto/me/MedioPagoDTO";

export interface TarjetaRequestDTO {
  numero: string;
  nombre: string;
  fechaVto: string;
  cvv: string;
  moneda: MonedaMedioPago;
  principal?: boolean;
}

export interface TarjetaResponseDTO {
  idTarjeta: number;
  idMedioPago?: number;
  tipo?: "TARJETA" | string;
  numeroEnmascarado: string;
  numero?: string;
  nombre: string;
  fechaVto: string;
  moneda: MonedaMedioPago;
  limiteCredito: number;
  principal?: boolean;
}
