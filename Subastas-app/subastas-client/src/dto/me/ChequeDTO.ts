import { MonedaMedioPago } from "@/src/dto/me/MedioPagoDTO";

export interface ChequeRequestDTO {
  identificacion: string;
  nroCheque: string;
  beneficiario: string;
  cuilCuit: string;
  saldo: number;
  moneda: MonedaMedioPago;
}

export interface ChequeResponseDTO {
  idCheque: number;
  idMedioPago?: number;
  tipo?: "CHEQUE" | string;
  identificacion: string;
  nroCheque: string;
  beneficiario: string;
  cuilCuit: string;
  saldo: number;
  moneda: MonedaMedioPago;
  estado?: string;
}
