import { BackendMoneda } from "@/src/utils/moneda";

export type MonedaMedioPago = BackendMoneda;

export interface MedioPagoResponseDTO {
  idMedioPago: number;
  tipo: "TARJETA" | "CHEQUE";
  descripcion: string;
  moneda: MonedaMedioPago;
  capacidad: number;
}
