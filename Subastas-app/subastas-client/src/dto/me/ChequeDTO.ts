export interface ChequeRequestDTO {
  identificacion: number;
  nroCheque: string;
  beneficiario: string;
  cuilCuit: string;
  saldo: number;
}

export interface ChequeResponseDTO {
  idMedioPago: number;
  tipo: "CHEQUE" | string;
  identificacion: number;
  nroCheque: string;
  beneficiario: string;
  cuilCuit: string;
  saldo: number;
}